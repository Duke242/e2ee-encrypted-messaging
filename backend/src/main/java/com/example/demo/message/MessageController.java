package com.example.demo.message;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/messages")
public class MessageController {

  private final MessageService messageService;

  @Autowired
  public MessageController(MessageService messageService) {
    this.messageService = messageService;
  }

  @PostMapping
  public ResponseEntity<Message> sendMessage(@RequestParam Long senderId,
      @RequestParam Long recipientId,
      @RequestBody String content) {
    Message sentMessage = messageService.sendMessage(senderId, recipientId, content);
    return ResponseEntity.ok(sentMessage);
  }

  @GetMapping("/conversation")
  public ResponseEntity<List<Message>> getConversation(@RequestParam Long user1Id,
      @RequestParam Long user2Id) {
    List<Message> conversation = messageService.getConversation(user1Id, user2Id);
    return ResponseEntity.ok(conversation);
  }
}