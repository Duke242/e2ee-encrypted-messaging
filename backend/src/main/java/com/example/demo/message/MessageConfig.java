package com.example.demo.message;

import com.example.demo.profile.Profile;
import com.example.demo.profile.ProfileRepository;
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

  // @Bean
  // CommandLineRunner messageCommandLineRunner(
  // MessageRepository messageRepository,
  // ProfileRepository profileRepository,
  // SignalService signalService) {
  // return args -> {
  // try {
  // Profile profile1 = profileRepository.findProfileByEmail("a@a.com")
  // .orElseGet(() -> profileRepository
  // .save(new Profile("a@a.com",
  // "$2a$10$33vRlc7Em5A5XGLHuvyD0.UMM1AODwVu7N5t5UcfGNKYTOGMREL2i")));

  // Profile profile2 = profileRepository.findProfileByEmail("b@b.com")
  // .orElseGet(() -> profileRepository
  // .save(new Profile("b@b.com",
  // "$2a$10$HjJSZZtYW/BYu8fByi8ypegaQsKPSpQBKmuiM3FsvjsTDgDQOU9se")));

  // generateKeysForProfile(signalService, profile1);
  // generateKeysForProfile(signalService, profile2);

  // establishSession(signalService, profile1, profile2);
  // establishSession(signalService, profile2, profile1);

  // List<String> messages = List.of(
  // "Hello, how are you?",
  // "I'm doing well, thank you!",
  // "This is not an encrypted message.",
  // "How was your day?",
  // "It was great, thanks for asking.",
  // "I'm looking forward to the weekend.",
  // "Me too, I need a break.",
  // "Do you have any plans?",
  // "Not yet, but I'm thinking of going to the beach.",
  // "That sounds like fun. I might join you.");

  // for (int i = 0; i < messages.size(); i++) {
  // Profile sender = i % 2 == 0 ? profile1 : profile2;
  // Profile recipient = i % 2 == 0 ? profile2 : profile1;

  // try {
  // SignalProtocolAddress recipientAddress = new
  // SignalProtocolAddress(recipient.getEmail(), 1);
  // byte[] paddedMessage = padMessage(messages.get(i).getBytes());

  // logger.debug("Encrypting message for recipient: {}", recipient.getEmail());
  // byte[] encryptedContent = signalService.encryptMessage(recipientAddress,
  // paddedMessage).serialize();
  // logger.debug("Message encrypted successfully");

  // logger.debug("Saving message to repository");
  // Message message = new Message(sender, recipient, null, true);
  // message.setEncryptedContent(encryptedContent);
  // messageRepository.save(message);
  // logger.debug("Message saved successfully");

  // logger.info("Message saved successfully: {} -> {}", sender.getEmail(),
  // recipient.getEmail());
  // } catch (Exception e) {
  // logger.error("Error processing message: {} -> {}", sender.getEmail(),
  // recipient.getEmail(), e);
  // }
  // }
  // } catch (Exception e) {
  // logger.error("Error in CommandLineRunner", e);
  // }
  // };
  // }

  private void generateKeysForProfile(Profile profile) {
    try {

      logger.info("Generated keys for {}", profile.getEmail());
    } catch (Exception e) {
      logger.error("Error generating keys for {}", profile.getEmail(), e);
    }
  }

  // public void establishSession( Profile sender,
  // Profile recipient) throws Exception {
  // PreKeyBundle recipientPreKeyBundle = fetchPreKeyBundle(recipient);
  // SignalProtocolAddress recipientAddress = new
  // SignalProtocolAddress(recipient.getEmail(), 1);
  // signalService.processPreKeyBundle(recipientPreKeyBundle, recipientAddress);
  // }

}