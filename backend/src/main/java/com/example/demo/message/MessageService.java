package com.example.demo.message;

import java.util.ArrayList;
import java.util.HashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.profile.Profile;
import com.example.demo.profile.ProfileRepository;
import java.util.*;

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

  public Map<Long, List<Message>> getAllUserConversations(Long userId) {
    List<Message> allUserMessages = messageRepository.findAllByUserIdInvolved(userId);

    Map<Long, List<Message>> conversationsMap = new HashMap<>();

    for (Message message : allUserMessages) {
      Long otherUserId = message.getSenderId().equals(userId) ? message.getRecipientId() : message.getSenderId();
      conversationsMap.computeIfAbsent(otherUserId, k -> new ArrayList<>()).add(message);
    }

    for (List<Message> conversation : conversationsMap.values()) {
      conversation.sort(Comparator.comparing(Message::getTimestamp));
    }

    return conversationsMap;
  }

  public void sendMessage(Long senderId, String recipientEmail, String encryptedContent) {
    try {

      Profile senderProfile = profileRepository.findById(senderId)
          .orElseThrow(() -> new IllegalArgumentException("Invalid sender ID"));
      Profile recipientProfile = profileRepository.findByEmail(recipientEmail)
          .orElseThrow(() -> new IllegalArgumentException("Invalid recipient email"));

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