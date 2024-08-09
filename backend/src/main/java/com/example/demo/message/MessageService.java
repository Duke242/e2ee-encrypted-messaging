package com.example.demo.message;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.example.demo.profile.Profile;
import com.example.demo.profile.ProfileRepository;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class MessageService {

  private final MessageRepository messageRepository;
  private final ProfileRepository profileRepository;

  @Autowired
  public MessageService(MessageRepository messageRepository, ProfileRepository profileRepository) {
    this.messageRepository = messageRepository;
    this.profileRepository = profileRepository;
  }

  public Message sendMessage(Long senderId, Long recipientId, String content) {
    Profile sender = profileRepository.findById(senderId)
        .orElseThrow(() -> new IllegalArgumentException("Sender not found"));
    Profile recipient = profileRepository.findById(recipientId)
        .orElseThrow(() -> new IllegalArgumentException("Recipient not found"));

    boolean isEncrypted = false;

    Message message = new Message(sender, recipient, content, isEncrypted);
    return messageRepository.save(message);
  }

  public List<Message> getConversation(Long user1Id, Long user2Id) {
    Profile user1 = profileRepository.findById(user1Id)
        .orElseThrow(() -> new IllegalArgumentException("User1 not found"));
    Profile user2 = profileRepository.findById(user2Id)
        .orElseThrow(() -> new IllegalArgumentException("User2 not found"));

    List<Message> sentMessages = messageRepository.findBySenderAndRecipientOrderByTimestampDesc(user1, user2);
    List<Message> receivedMessages = messageRepository.findBySenderAndRecipientOrderByTimestampDesc(user2, user1);

    sentMessages.addAll(receivedMessages);
    sentMessages.sort((m1, m2) -> m2.getTimestamp().compareTo(m1.getTimestamp()));

    return sentMessages;
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
}