package com.example.demo.profile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.whispersystems.libsignal.InvalidKeyException;

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

  @GetMapping
  public ResponseEntity<List<ProfileDTO>> getAllProfiles() {
    logger.info("Received request to get all profiles");
    List<ProfileDTO> profiles = profileService.getProfiles();

    logger.info("Returning {} profiles", profiles.size());
    return ResponseEntity.ok(profiles);
  }

  @PostMapping
  public ResponseEntity<?> addNewProfile(@RequestBody Profile profile) {
    try {
      profileService.addProfile(profile);
      return ResponseEntity.status(HttpStatus.CREATED).body("Profile created successfully");
    } catch (IllegalStateException e) {
      logger.warn("Attempt to create profile with existing email", e);
      return ResponseEntity.status(HttpStatus.CONFLICT).body("Email already exists");
    } catch (InvalidKeyException e) {
      logger.error("Failed to generate cryptographic keys", e);
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error generating cryptographic keys");
    } catch (IOException e) {
      logger.error("IO error while creating profile", e);
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("IO error while creating profile");
    } catch (Exception e) {
      logger.error("Unexpected error while creating profile", e);
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Unexpected error occurred");
    }
  }

  @DeleteMapping(path = "{profileId}")
  public void deleteProfile(@PathVariable("profileId") Long profileId) {
    profileService.deleteProfile(profileId);

  }

  @PutMapping(path = "{profileId}")
  public void updateProfile(@PathVariable("profileId") Long profileId,
      @RequestParam(required = false) String email) {
    profileService.updateProfile(profileId, email);
  }

}