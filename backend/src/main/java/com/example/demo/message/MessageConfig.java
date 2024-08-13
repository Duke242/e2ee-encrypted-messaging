package com.example.demo.message;

import com.example.demo.profile.Profile;
import com.example.demo.profile.ProfileRepository;
import com.example.demo.encryption.SignalService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.whispersystems.libsignal.SignalProtocolAddress;
import org.whispersystems.libsignal.state.PreKeyBundle;
import org.whispersystems.libsignal.state.PreKeyRecord;
import org.whispersystems.libsignal.state.SignedPreKeyRecord;
import org.whispersystems.libsignal.IdentityKeyPair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@Configuration
public class MessageConfig {

  private static final Logger logger = LoggerFactory.getLogger(MessageConfig.class);

  @Bean
  CommandLineRunner messageCommandLineRunner(
      MessageRepository messageRepository,
      ProfileRepository profileRepository,
      SignalService signalService) {
    return args -> {
      try {
        Profile profile1 = profileRepository.findProfileByEmail("a@a.com")
            .orElseGet(() -> profileRepository
                .save(new Profile("a@a.com",
                    "$2a$10$33vRlc7Em5A5XGLHuvyD0.UMM1AODwVu7N5t5UcfGNKYTOGMREL2i")));

        Profile profile2 = profileRepository.findProfileByEmail("b@b.com")
            .orElseGet(() -> profileRepository
                .save(new Profile("b@b.com",
                    "$2a$10$HjJSZZtYW/BYu8fByi8ypegaQsKPSpQBKmuiM3FsvjsTDgDQOU9se")));

        generateKeysForProfile(signalService, profile1);
        generateKeysForProfile(signalService, profile2);

        establishSession(signalService, profile1, profile2);
        establishSession(signalService, profile2, profile1);

        List<String> messages = List.of(
            "Hello, how are you?",
            "I'm doing well, thank you!",
            "This is not an encrypted message.",
            "How was your day?",
            "It was great, thanks for asking.",
            "I'm looking forward to the weekend.",
            "Me too, I need a break.",
            "Do you have any plans?",
            "Not yet, but I'm thinking of going to the beach.",
            "That sounds like fun. I might join you.");

        for (int i = 0; i < messages.size(); i++) {
          Profile sender = i % 2 == 0 ? profile1 : profile2;
          Profile recipient = i % 2 == 0 ? profile2 : profile1;

          try {
            SignalProtocolAddress recipientAddress = new SignalProtocolAddress(recipient.getEmail(), 1);
            byte[] paddedMessage = padMessage(messages.get(i).getBytes());

            logger.debug("Encrypting message for recipient: {}", recipient.getEmail());
            byte[] encryptedContent = signalService.encryptMessage(recipientAddress, paddedMessage).serialize();
            logger.debug("Message encrypted successfully");

            logger.debug("Saving message to repository");
            Message message = new Message(sender, recipient, null, true);
            message.setEncryptedContent(encryptedContent);
            messageRepository.save(message);
            logger.debug("Message saved successfully");

            logger.info("Message saved successfully: {} -> {}", sender.getEmail(),
                recipient.getEmail());
          } catch (Exception e) {
            logger.error("Error processing message: {} -> {}", sender.getEmail(),
                recipient.getEmail(), e);
          }
        }
      } catch (Exception e) {
        logger.error("Error in CommandLineRunner", e);
      }
    };
  }

  private void generateKeysForProfile(SignalService signalService, Profile profile) {
    try {
      IdentityKeyPair identityKeyPair = signalService.generateIdentityKeyPair();
      List<PreKeyRecord> preKeys = signalService.generatePreKeys();
      SignedPreKeyRecord signedPreKey = signalService.generateSignedPreKey(identityKeyPair, 1);

      // Store these keys securely, associated with the profile
      // This is a simplified example - in a real application, you'd use secure
      // storage
      profile.setIdentityKeyPair(identityKeyPair);
      profile.setPreKeys(preKeys);
      profile.setSignedPreKey(signedPreKey);
      profile.setRegistrationId(signalService.generateRegistrationId(false));
      profile.setDeviceId(1); // Assuming single device per user for simplicity

      logger.info("Generated keys for {}", profile.getEmail());
    } catch (Exception e) {
      logger.error("Error generating keys for {}", profile.getEmail(), e);
    }
  }

  private void establishSession(SignalService signalService, Profile sender, Profile recipient) {
    try {
      // Fetch recipient's pre-key bundle
      PreKeyBundle recipientPreKeyBundle = fetchPreKeyBundle(recipient);

      if (recipientPreKeyBundle == null) {
        logger.error("Failed to fetch pre-key bundle for {}", recipient.getEmail());
        return;
      }

      SignalProtocolAddress recipientAddress = new SignalProtocolAddress(recipient.getEmail(), 1);

      // Process the pre-key bundle to establish a session
      signalService.processPreKeyBundle(recipientPreKeyBundle, recipientAddress);

      logger.info("Established session from {} to {}", sender.getEmail(), recipient.getEmail());
    } catch (Exception e) {
      logger.error("Error establishing session from {} to {}: {}",
          sender.getEmail(), recipient.getEmail(), e.getMessage(), e);
    }
  }

  private PreKeyBundle fetchPreKeyBundle(Profile profile) {
    // In a real application, you would fetch this from a server
    // This is a simplified example
    try {
      IdentityKeyPair identityKeyPair = profile.getIdentityKeyPair();
      List<PreKeyRecord> preKeys = profile.getPreKeys();
      SignedPreKeyRecord signedPreKey = profile.getSignedPreKey();

      if (identityKeyPair == null || preKeys.isEmpty() || signedPreKey == null) {
        logger.error("Missing keys for profile: {}", profile.getEmail());
        return null;
      }

      PreKeyRecord preKey = preKeys.get(0); // Use the first pre-key for simplicity

      return new PreKeyBundle(
          profile.getRegistrationId(),
          profile.getDeviceId(),
          preKey.getId(),
          preKey.getKeyPair().getPublicKey(),
          signedPreKey.getId(),
          signedPreKey.getKeyPair().getPublicKey(),
          signedPreKey.getSignature(),
          identityKeyPair.getPublicKey());
    } catch (Exception e) {
      logger.error("Error creating PreKeyBundle for {}: {}", profile.getEmail(), e.getMessage(), e);
      return null;
    }
  }

  private byte[] padMessage(byte[] message) {
    // Implement proper padding here
    // For this example, we'll just return the original message
    return message;
  }
}