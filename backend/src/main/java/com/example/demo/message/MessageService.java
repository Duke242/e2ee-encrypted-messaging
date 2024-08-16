package com.example.demo.message;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.profile.Profile;
import com.example.demo.profile.ProfileRepository;

@Service
public class MessageService {

  private final MessageRepository messageRepository;
  private final ProfileRepository profileRepository;

  private final MessageConfig messageConfig;

  private static final Logger logger = LoggerFactory.getLogger(MessageConfig.class);

  @Autowired
  public MessageService(MessageRepository messageRepository, ProfileRepository profileRepository,
      MessageConfig messageConfig) {
    this.messageRepository = messageRepository;
    this.profileRepository = profileRepository;
    this.messageConfig = messageConfig;
  }

  public void sendMessage(Long senderId, String recipientEmail, String encryptedContent) {
    try {
      // Fetch sender and recipient profiles
      Profile senderProfile = profileRepository.findById(senderId)
          .orElseThrow(() -> new IllegalArgumentException("Invalid sender ID"));
      Profile recipientProfile = profileRepository.findByEmail(recipientEmail)
          .orElseThrow(() -> new IllegalArgumentException("Invalid recipient email"));

      // Create and save the message
      Message message = new Message();
      message.setSender(senderProfile);
      message.setRecipient(recipientProfile);
      message.setContent(encryptedContent);
      messageRepository.save(message);
    } catch (Exception e) {
      logger.error("Error sending message", e);
      throw new RuntimeException("Error sending message", e);
    }
  }

}