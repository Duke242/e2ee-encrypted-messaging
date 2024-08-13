package com.example.demo.encryption;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.List;

import org.springframework.stereotype.Service;
import org.whispersystems.libsignal.*;
import org.whispersystems.libsignal.protocol.CiphertextMessage;
import org.whispersystems.libsignal.protocol.PreKeySignalMessage;
import org.whispersystems.libsignal.protocol.SignalMessage;
import org.whispersystems.libsignal.state.PreKeyBundle;
import org.whispersystems.libsignal.state.PreKeyRecord;
import org.whispersystems.libsignal.state.SignalProtocolStore;
import org.whispersystems.libsignal.state.SignedPreKeyRecord;
import org.whispersystems.libsignal.util.KeyHelper;;

@Service
public class SignalService {
  private final SignalProtocolStore protocolStore;

  public SignalService(SignalProtocolStore protocolStore) {
    this.protocolStore = protocolStore;
  }

  public byte[] encryptMessage(String messsage, SignalProtocolAddress recipientAddress) throws Exception {
    SessionCipher sessionCipher = new SessionCipher(protocolStore, recipientAddress);
    return sessionCipher.encrypt(messsage.getBytes()).serialize();
  }

  public int generateRegistrationId(boolean extendedRange) throws NoSuchAlgorithmException {
    if (extendedRange) {
      return SecureRandom.getInstance("SHA1PRNG").nextInt(Integer.MAX_VALUE - 1) + 1;
    } else {
      return SecureRandom.getInstance("SHA1PRNG").nextInt(16380) + 1;
    }
  }

  public String decryptMessage(byte[] encryptedMessage, SignalProtocolAddress senderAddress) throws Exception {
    SessionCipher sessionCipher = new SessionCipher(protocolStore, senderAddress);
    byte[] decrypted = sessionCipher.decrypt(new SignalMessage(encryptedMessage));
    return new String(decrypted);
  }

  public IdentityKeyPair generateIdentityKeyPair() {
    return KeyHelper.generateIdentityKeyPair();
  }

  public List<PreKeyRecord> generatePreKeys() {
    return KeyHelper.generatePreKeys(0, 100);
  }

  public SignedPreKeyRecord generateSignedPreKey(IdentityKeyPair identityKeyPair, int signedPreKeyId)
      throws InvalidKeyException {
    return KeyHelper.generateSignedPreKey(identityKeyPair, signedPreKeyId);
  }

  public void processPreKeyBundle(PreKeyBundle preKeyBundle, SignalProtocolAddress address)
      throws InvalidKeyException, UntrustedIdentityException {
    SessionBuilder sessionBuilder = new SessionBuilder(protocolStore, address);
    sessionBuilder.process(preKeyBundle);
  }

  public CiphertextMessage encryptMessage(SignalProtocolAddress address, byte[] paddedMessage)
      throws UntrustedIdentityException {
    SessionCipher sessionCipher = new SessionCipher(protocolStore, address);
    return sessionCipher.encrypt(paddedMessage);
  }

  public byte[] decryptMessage(SignalProtocolAddress address, byte[] ciphertext)
      throws InvalidMessageException, DuplicateMessageException, LegacyMessageException,
      InvalidKeyIdException, InvalidKeyException, UntrustedIdentityException, NoSessionException,
      InvalidVersionException {
    SessionCipher sessionCipher = new SessionCipher(protocolStore, address);

    try {
      return sessionCipher.decrypt(new SignalMessage(ciphertext));
    } catch (InvalidMessageException e) {
      return sessionCipher.decrypt(new PreKeySignalMessage(ciphertext));
    }
  }
}
