package com.example.demo.profile;

import jakarta.persistence.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.whispersystems.libsignal.IdentityKeyPair;
import org.whispersystems.libsignal.InvalidKeyException;
import org.whispersystems.libsignal.state.PreKeyRecord;
import org.whispersystems.libsignal.state.SignedPreKeyRecord;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Entity
@Table(name = "profile")
public class Profile implements UserDetails {

  @Id
  @SequenceGenerator(name = "profile_sequence", sequenceName = "profile_sequence", allocationSize = 1)
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "profile_sequence")
  private Long id;
  private String email;
  private String password;
  private byte[] identityKeyPairSerialized;
  private byte[] preKeysSerialized;
  private byte[] signedPreKeySerialized;

  private int registrationId;
  private int deviceId;

  public Profile() {
  }

  public Profile(String email, String password) {
    this.email = email;
    this.password = password;
  }

  public Profile(Long id, String email, String password) {
    this.id = id;
    this.email = email;
    this.password = password;
  }

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return List.of(new SimpleGrantedAuthority("ROLE_USER"));
  }

  @Override
  public String getPassword() {
    return password;
  }

  @Override
  public String getUsername() {
    return email;
  }

  @Override
  public boolean isAccountNonExpired() {
    return true;
  }

  @Override
  public boolean isAccountNonLocked() {
    return true;
  }

  @Override
  public boolean isCredentialsNonExpired() {
    return true;
  }

  @Override
  public boolean isEnabled() {
    return true;
  }

  @Override
  public String toString() {
    return "Profile{" +
        "email='" + email + '\'' +
        ", password='" + password + '\'' +
        '}';
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public void setIdentityKeyPair(IdentityKeyPair identityKeyPair) {
    this.identityKeyPairSerialized = (byte[]) identityKeyPair.serialize();
  }

  public IdentityKeyPair getIdentityKeyPair() throws InvalidKeyException {
    return new IdentityKeyPair(identityKeyPairSerialized);
  }

  public void setPreKeys(List<PreKeyRecord> preKeys) {
    byte[][] serializedPreKeys = preKeys.stream().map(PreKeyRecord::serialize).toArray(byte[][]::new);
    this.preKeysSerialized = serializeByteArrays(serializedPreKeys);
  }

  public List<PreKeyRecord> getPreKeys() throws IOException {
    byte[][] deserializedPreKeys = deserializeByteArrays(this.preKeysSerialized);
    return Arrays.stream(deserializedPreKeys)
        .map(bytes -> {
          try {
            return new PreKeyRecord(bytes);
          } catch (IOException e) {
            throw new UncheckedIOException(e);
          }
        })
        .collect(Collectors.toList());
  }

  public void setSignedPreKey(SignedPreKeyRecord signedPreKey) {
    this.signedPreKeySerialized = signedPreKey.serialize();
  }

  public SignedPreKeyRecord getSignedPreKey() throws IOException {
    return new SignedPreKeyRecord(signedPreKeySerialized);
  }

  public int getRegistrationId() {
    return registrationId;
  }

  public void setRegistrationId(int registrationId) {
    this.registrationId = registrationId;
  }

  public int getDeviceId() {
    return deviceId;
  }

  public void setDeviceId(int deviceId) {
    this.deviceId = deviceId;
  }

  // Helper methods for serialization/deserialization of byte arrays
  private byte[] serializeByteArrays(byte[][] arrays) {
    try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
      for (byte[] array : arrays) {
        outputStream.write(intToByteArray(array.length));
        outputStream.write(array);
      }
      return outputStream.toByteArray();
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  private byte[][] deserializeByteArrays(byte[] serialized) {
    try (ByteArrayInputStream inputStream = new ByteArrayInputStream(serialized);
        DataInputStream dataInputStream = new DataInputStream(inputStream)) {

      List<byte[]> result = new ArrayList<>();
      while (dataInputStream.available() > 0) {
        int length = dataInputStream.readInt();
        byte[] array = new byte[length];
        dataInputStream.readFully(array);
        result.add(array);
      }
      return result.toArray(new byte[0][]);

    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  private byte[] intToByteArray(int value) {
    return new byte[] {
        (byte) (value >> 24),
        (byte) (value >> 16),
        (byte) (value >> 8),
        (byte) value
    };
  }
}
