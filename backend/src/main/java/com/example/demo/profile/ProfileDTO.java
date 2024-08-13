package com.example.demo.profile;

public class ProfileDTO {
  private Long id;
  private String email;

  public ProfileDTO(Long id, String email) {
    this.id = id;
    this.email = email;
  }

  // Getters
  public Long getId() {
    return id;
  }

  public String getEmail() {
    return email;
  }

  // Setters
  public void setId(Long id) {
    this.id = id;
  }

  public void setEmail(String email) {
    this.email = email;
  }
}