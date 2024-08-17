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