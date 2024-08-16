package com.example.demo.message;

import jakarta.persistence.*;
import com.example.demo.profile.Profile;
import java.time.LocalDateTime;

@Entity
@Table(name = "messages")
public class Message {

  @Id
  @SequenceGenerator(name = "message_sequence", sequenceName = "message_sequence", allocationSize = 1)
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "message_sequence")
  private Long id;

  @ManyToOne
  @JoinColumn(name = "sender_id", nullable = false)
  private Profile sender;

  @ManyToOne
  @JoinColumn(name = "recipient_id", nullable = false)
  private Profile recipient;

  @Column(nullable = true)
  private String content;

  @Column(nullable = false)
  private LocalDateTime timestamp;

  @Column(nullable = true, columnDefinition = "BYTEA")
  private byte[] encryptedContent;

  @Column(nullable = true)
  private String encryptionMetadata;

  public Message() {
    this.timestamp = LocalDateTime.now();
  }

  public Message(Profile sender, Profile recipient, String content) {
    this.sender = sender;
    this.recipient = recipient;
    this.content = content;
    this.timestamp = LocalDateTime.now();
  }

  public Message(Profile sender, Profile recipient, byte[] encryptedContent, String encryptionMetadata) {
    this.sender = sender;
    this.recipient = recipient;
    this.encryptedContent = encryptedContent;
    this.encryptionMetadata = encryptionMetadata;
    this.timestamp = LocalDateTime.now();
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Profile getSender() {
    return sender;
  }

  public void setSender(Profile sender) {
    this.sender = sender;
  }

  public Long getSenderId() {
    return sender != null ? sender.getId() : null;
  }

  public Profile getRecipient() {
    return recipient;
  }

  public void setRecipient(Profile recipient) {
    this.recipient = recipient;
  }

  public Long getRecipientId() {
    return recipient != null ? recipient.getId() : null;
  }

  public String getContent() {
    return content;
  }

  public void setContent(String content) {
    this.content = content;
  }

  public LocalDateTime getTimestamp() {
    return timestamp;
  }

  public void setTimestamp(LocalDateTime timestamp) {
    this.timestamp = timestamp;
  }

  public byte[] getEncryptedContent() {
    return encryptedContent;
  }

  public void setEncryptedContent(byte[] encryptedContent) {
    this.encryptedContent = encryptedContent;
  }

  public String getEncryptionMetadata() {
    return encryptionMetadata;
  }

  public void setEncryptionMetadata(String encryptionMetadata) {
    this.encryptionMetadata = encryptionMetadata;
  }

}
