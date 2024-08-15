package com.example.demo.profile;

public class ProfileDTO {
  private Long id;
  private String email;
  private String publicKey;

  public ProfileDTO(Long id, String email, String publicKey) {
    this.id = id;
    this.email = email;
    this.publicKey = publicKey;
  }

  // Getters and setters
  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getPublicKey() {
    return publicKey;
  }

  public void setPublicKey(String publicKey) {
    this.publicKey = publicKey;
  }
}