package com.example.demo.profile;

import java.util.List;
import java.util.Optional;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.slf4j.Logger;

@RestController
@RequestMapping(path = "api/profiles")
public class ProfileController {

  private final ProfileService profileService;
  private static final Logger logger = LoggerFactory.getLogger(ProfileController.class);

  @Autowired
  public ProfileController(ProfileService profileService) {
    this.profileService = profileService;
  }

  @GetMapping("/emails")
  public Optional<List<String>> getProfileEmails() {
    return profileService.getProfileEmails();
  }

  @PostMapping("/email")
  public Optional<String> findEmailById(@RequestBody String id) {
    return profileService.findEmailById(id);
  }

  @GetMapping
  public ResponseEntity<List<ProfileDTO>> getAllProfiles() {
    logger.info("Received request to get all profiles");
    List<ProfileDTO> profiles = profileService.getProfiles();

    logger.info("Returning {} profiles", profiles.size());
    return ResponseEntity.ok(profiles);
  }

  @PutMapping("/{profileId}/public-key")
  public ResponseEntity<String> updatePublicKey(@PathVariable Long profileId, @RequestBody String newPublicKey) {
    try {
      profileService.updatePublicKey(profileId, newPublicKey);
      return ResponseEntity.ok("Public key updated successfully");
    } catch (IllegalStateException e) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
    }
  }

  @GetMapping("/{profileId}/public-key")
  public ResponseEntity<?> getPublicKey(@PathVariable Long profileId) {
    try {
      String publicKey = profileService.getPublicKey(profileId);
      return ResponseEntity.ok(publicKey);
    } catch (IllegalStateException e) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
    }
  }

  @DeleteMapping(path = "{profileId}")
  public ResponseEntity<?> deleteProfile(@PathVariable("profileId") Long profileId) {
    try {
      profileService.deleteProfile(profileId);
      return ResponseEntity.ok("Profile deleted successfully");
    } catch (IllegalStateException e) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
    }
  }

  @PutMapping(path = "{profileId}")
  public ResponseEntity<?> updateProfile(@PathVariable("profileId") Long profileId,
      @RequestParam(required = false) String email) {
    try {
      profileService.updateProfile(profileId, email);
      return ResponseEntity.ok("Profile updated successfully");
    } catch (IllegalStateException e) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
    }
  }
}

class SignupRequest {
  private String email;
  private String password;
  private String publicKey;

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

  public String getPublicKey() {
    return publicKey;
  }

  public void setPublicKey(String publicKey) {
    this.publicKey = publicKey;
  }
}
