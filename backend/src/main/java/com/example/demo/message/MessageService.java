package com.example.demo.message;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.whispersystems.libsignal.SignalProtocolAddress;

import com.example.demo.profile.Profile;
import com.example.demo.profile.ProfileRepository;
import com.example.demo.encryption.SignalService;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class MessageService {

  private final MessageRepository messageRepository;
  private final ProfileRepository profileRepository;
  private final SignalService signalService;

  @Autowired
  public MessageService(MessageRepository messageRepository, ProfileRepository profileRepository,
      SignalService signalService) {
    this.messageRepository = messageRepository;
    this.profileRepository = profileRepository;
    this.signalService = signalService;
  }

  public Message sendMessage(Long senderId, String recipientEmail, String content) throws Exception {
    Profile sender = profileRepository.findById(senderId)
        .orElseThrow(() -> new IllegalArgumentException("Sender not found"));
    Profile recipient = profileRepository.findProfileByEmail(recipientEmail)
        .orElseThrow(() -> new IllegalArgumentException("Recipient not found"));

    SignalProtocolAddress recipientAddress = new SignalProtocolAddress(recipient.getEmail(), 1);
    byte[] encryptedContent = signalService.encryptMessage(content, recipientAddress);

    Message message = new Message(sender, recipient, null, true);
    message.setEncryptedContent(encryptedContent);
    message.setEncryptionMetadata(recipientAddress.toString());

    return messageRepository.save(message);
  }

  public List<Message> getConversation(Long user1Id, Long user2Id) throws Exception {
    Profile user1 = profileRepository.findById(user1Id)
        .orElseThrow(() -> new IllegalArgumentException("User1 not found"));
    Profile user2 = profileRepository.findById(user2Id)
        .orElseThrow(() -> new IllegalArgumentException("User2 not found"));

    List<Message> sentMessages = messageRepository.findBySenderAndRecipientOrderByTimestampDesc(user1, user2);
    List<Message> receivedMessages = messageRepository.findBySenderAndRecipientOrderByTimestampDesc(user2, user1);

    List<Message> allMessages = new ArrayList<>(sentMessages);
    allMessages.addAll(receivedMessages);
    allMessages.sort((m1, m2) -> m2.getTimestamp().compareTo(m1.getTimestamp()));

    return decryptMessages(allMessages, user1);
  }

  public Map<Long, List<Message>> getAllUserConversations(Long userId) throws Exception {
    Profile user = profileRepository.findById(userId)
        .orElseThrow(() -> new IllegalArgumentException("User not found"));
    List<Message> allUserMessages = messageRepository.findAllByUserIdInvolved(userId);

    Map<Long, List<Message>> conversationsMap = new HashMap<>();

    for (Message message : allUserMessages) {
      Long otherUserId = message.getSenderId().equals(userId) ? message.getRecipientId() : message.getSenderId();
      conversationsMap.computeIfAbsent(otherUserId, k -> new ArrayList<>()).add(message);
    }

    for (Map.Entry<Long, List<Message>> entry : conversationsMap.entrySet()) {
      List<Message> decryptedConversation = decryptMessages(entry.getValue(), user);
      entry.setValue(decryptedConversation);
      decryptedConversation.sort(Comparator.comparing(Message::getTimestamp));
    }

    return conversationsMap;
  }

  private List<Message> decryptMessages(List<Message> messages, Profile currentUser) throws Exception {
    return messages.stream().map(message -> {
      try {
        SignalProtocolAddress senderAddress = new SignalProtocolAddress(message.getSender().getEmail(), 1);
        String decryptedContent = signalService.decryptMessage(message.getEncryptedContent(), senderAddress);
        message.setContent(decryptedContent);
        return message;
      } catch (Exception e) {

        System.err.println("Failed to decrypt message: " + e.getMessage());
        return message;
      }
    }).collect(Collectors.toList());
  }
}