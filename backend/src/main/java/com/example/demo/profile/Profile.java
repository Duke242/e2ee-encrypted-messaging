package com.example.demo.profile;

import jakarta.persistence.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Entity
@Table
public class Profile implements UserDetails {

  @Id
  @SequenceGenerator(name = "profile_sequence", sequenceName = "profile_sequence", allocationSize = 1)
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "profile_sequence")
  private Long id;

  private String email;
  private String password;

  @Column(columnDefinition = "TEXT")
  private String publicKey;

  private int registrationId;
  private int deviceId;

  public Profile() {
  }

  public Profile(String email, String password, String publicKey) {
    this.email = email;
    this.password = password;
    this.publicKey = publicKey;
  }

  public Profile(Long id, String email, String password, String publicKey) {
    this.id = id;
    this.email = email;
    this.password = password;
    this.publicKey = publicKey;
  }

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return List.of(new SimpleGrantedAuthority("ROLE_USER"));
  }

  @Override
  public String getPassword() {
    return password;
  }

  @Override
  public String getUsername() {
    return email;
  }

  @Override
  public boolean isAccountNonExpired() {
    return true;
  }

  @Override
  public boolean isAccountNonLocked() {
    return true;
  }

  @Override
  public boolean isCredentialsNonExpired() {
    return true;
  }

  @Override
  public boolean isEnabled() {
    return true;
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

  public void setPassword(String password) {
    this.password = password;
  }

  public String getPublicKey() {
    return publicKey;
  }

  public void setPublicKey(String publicKey) {
    this.publicKey = publicKey;
  }

  public int getRegistrationId() {
    return registrationId;
  }

  public void setRegistrationId(int registrationId) {
    this.registrationId = registrationId;
  }

  public int getDeviceId() {
    return deviceId;
  }

  public void setDeviceId(int deviceId) {
    this.deviceId = deviceId;
  }

  @Override
  public String toString() {
    return "Profile{" +
        "id=" + id +
        ", email='" + email + '\'' +
        ", registrationId=" + registrationId +
        ", deviceId=" + deviceId +
        ", publicKey='" + publicKey + '\'' +
        '}';
  }
}