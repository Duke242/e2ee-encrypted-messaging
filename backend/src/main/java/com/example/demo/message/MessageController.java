package com.example.demo.message;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/api/messages")
public class MessageController {

  private static final Logger logger = LoggerFactory.getLogger(MessageController.class);

  private final MessageService messageService;

  @Autowired
  public MessageController(MessageService messageService) {
    this.messageService = messageService;
  }

  @PostMapping
  public ResponseEntity<?> sendMessage(@RequestParam Long senderId,
      @RequestParam Long recipientId,
      @RequestBody String content) {
    try {
      Message sentMessage = messageService.sendMessage(senderId, recipientId, content);
      return ResponseEntity.ok(sentMessage);
    } catch (Exception e) {
      logger.error("Error in sendMessage", e);
      return ResponseEntity
          .status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body(Map.of("error", e.getMessage()));
    }
  }

  @GetMapping("/conversation")
  public ResponseEntity<?> getConversation(@RequestParam Long user1Id,
      @RequestParam Long user2Id) {
    try {
      List<Message> conversation = messageService.getConversation(user1Id, user2Id);
      return ResponseEntity.ok(conversation);
    } catch (Exception e) {
      logger.error("Error in getConversation", e);
      return ResponseEntity
          .status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body(Map.of("error", e.getMessage()));
    }
  }

  @GetMapping("/user/{userId}/conversations")
  public ResponseEntity<?> getAllUserConversations(@PathVariable Long userId) {
    logger.info("Entering getAllUserConversations method with userId: {}", userId);
    try {
      logger.info("Calling messageService.getAllUserConversations");
      Map<Long, List<Message>> conversations = messageService.getAllUserConversations(userId);
      logger.info("Received conversations from service. Size: {}", conversations.size());
      logger.info("Conversations: {}", conversations);
      return ResponseEntity.ok(conversations);
    } catch (Exception e) {
      logger.error("Error in getAllUserConversations", e);
      return ResponseEntity
          .status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body(Map.of("error", e.getMessage()));
    } finally {
      logger.info("Exiting getAllUserConversations method");
    }
  }
}