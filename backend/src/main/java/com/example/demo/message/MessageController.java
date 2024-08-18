package com.example.demo.message;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.fasterxml.jackson.databind.JsonNode;

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

  // @GetMapping("/conversation")

  @GetMapping("/user/{userId}/conversations")
  public ResponseEntity<?> getAllUserConversations(@PathVariable Long userId) {
    try {
      Map<Long, List<Message>> conversations = messageService.getAllUserConversations(userId);
      return ResponseEntity.ok(conversations);
    } catch (Exception e) {
      logger.error("Error in getAllUserConversations", e);
      return ResponseEntity
          .status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body(Map.of("error", e.getMessage()));
    }
  }

  @PostMapping("/send")
  public ResponseEntity<?> createMessage(@RequestBody JsonNode requestBody) {
    try {
      Long userId = requestBody.get("senderId").asLong();
      String recipientEmail = requestBody.get("recipientEmail").asText();
      String content = requestBody.get("content").asText();

      messageService.sendMessage(userId, recipientEmail,
          content);
      return ResponseEntity.ok("Sent");
    } catch (Exception e) {
      return ResponseEntity.badRequest().body("Error creating message: " +
          e.getMessage());
    }
  }
}