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

  @PostMapping("/findId")
  public ResponseEntity<?> findIdByEmail(@RequestBody FindIdRequest request) {
      System.out.println("Attempting to find profile ID by email: " + request.getEmail());
      
      // Fetch the profile based on the email
      Optional<Profile> profile = profileService.findIdByEmail(request.getEmail());
  
      // Extract the profile ID if present
      if (profile.isPresent()) {
          Long profileId = profile.get().getId();
          return ResponseEntity.ok(profileId);
      } else {
          // Handle the case where the profile is not found
          return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Profile not found");
      }
  }
  

  @GetMapping("/emails")
  public Optional<List<String>> getProfileEmails() {
    return profileService.getProfileEmails();
  }

  @PostMapping("/email")
  public Optional<String> findEmailById(@RequestBody String id) {
    try {
      Long numericId = Long.parseLong(id);
      return profileService.findEmailById(numericId);
    } catch (NumberFormatException e) {
      return Optional.empty();
    }
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

class FindIdRequest {
  private String email;

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
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
