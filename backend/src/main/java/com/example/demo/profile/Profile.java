package com.example.demo.profile;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;

@Entity
@Table
public class Profile {

  @Id
  @SequenceGenerator(name = "profile_sequence", sequenceName = "profile_sequence", allocationSize = 1)
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "profile_sequence")
  private Long id;
  private String email;
  private String password;

  public Profile() {
  }

  public Profile(String email, String password) {
    this.email = email;
    this.password = password;
  }

  public Profile(Long id, String email, String password) {
    this.id = id;
    this.email = email;
    this.password = password;
  }

  @Override
  public String toString() {
    return "Profile{" +
        "email='" + email + '\'' +
        ", password='" + password + '\'' +
        '}';
  }

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

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }
}