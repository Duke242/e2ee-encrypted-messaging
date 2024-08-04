package com.example.demo.message;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.example.demo.profile.Profile;
import com.example.demo.profile.ProfileRepository;
import java.util.List;

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

    // TODO: Implement encryption using Signal Protocol
    boolean isEncrypted = false; // Placeholder

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
}