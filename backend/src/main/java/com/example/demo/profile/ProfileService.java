package com.example.demo.profile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.whispersystems.libsignal.IdentityKeyPair;
import org.whispersystems.libsignal.InvalidKeyException;
import org.whispersystems.libsignal.state.PreKeyRecord;
import org.whispersystems.libsignal.state.SignedPreKeyRecord;
import org.whispersystems.libsignal.util.KeyHelper;

import jakarta.transaction.Transactional;

@Service
public class ProfileService {

  private final ProfileRepository profileRepository;
  private final PasswordEncoder passwordEncoder;
  private static final Logger logger = LoggerFactory.getLogger(ProfileService.class);

  @Autowired
  public ProfileService(ProfileRepository profileRepository, PasswordEncoder passwordEncoder) {
    this.profileRepository = profileRepository;
    this.passwordEncoder = passwordEncoder;
  }

  public List<ProfileDTO> getProfiles() {
    logger.info("Retrieving all profiles");
    List<Profile> profiles = profileRepository.findAll();

    logger.info("Number of profiles retrieved: {}", profiles.size());

    List<ProfileDTO> profileDTOs = profiles.stream()
        .map(this::convertToDTO)
        .collect(Collectors.toList());

    logger.info("Number of profile DTOs: {}", profileDTOs.size());

    return profileDTOs;
  }

  private ProfileDTO convertToDTO(Profile profile) {
    return new ProfileDTO(profile.getId(), profile.getEmail());
  }

  public Optional<List<String>> getProfileEmails() {
    logger.info("Retrieving list of profile emails");
    Optional<List<String>> emails = profileRepository.findAllEmails();
    return emails;
  }

  public void addProfile(Profile profile) throws IllegalStateException, InvalidKeyException, IOException {
    Optional<Profile> profileOptional = profileRepository.findProfileByEmail(profile.getEmail());
    if (profileOptional.isPresent()) {
      throw new IllegalStateException("Email already exists");
    }

    String encodedPassword = passwordEncoder.encode(profile.getPassword());
    profile.setPassword(encodedPassword);

    // Initialize Signal Protocol-related fields if they're null
    if (profile.getIdentityKeyPair() == null) {
      IdentityKeyPair identityKeyPair = KeyHelper.generateIdentityKeyPair();
      profile.setIdentityKeyPair(identityKeyPair);
    }

    if (profile.getRegistrationId() == 0) {
      int registrationId = KeyHelper.generateRegistrationId(false);
      profile.setRegistrationId(registrationId);
    }

    if (profile.getPreKeys() == null || profile.getPreKeys().isEmpty()) {
      List<PreKeyRecord> preKeys = KeyHelper.generatePreKeys(0, 100);
      profile.setPreKeys(preKeys);
    }

    if (profile.getSignedPreKey() == null) {
      SignedPreKeyRecord signedPreKey = KeyHelper.generateSignedPreKey(profile.getIdentityKeyPair(), 0);
      profile.setSignedPreKey(signedPreKey);
    }

    profileRepository.save(profile);
  }

  public void deleteProfile(Long profileId) {
    boolean exists = profileRepository.existsById(profileId);
    if (!exists) {
      throw new IllegalStateException("Profile does not exist");
    }

    profileRepository.deleteById(profileId);
  }

  @Transactional
  public void updateProfile(Long profileId, String email) {
    Profile profile = profileRepository.findById(profileId)
        .orElseThrow(() -> new IllegalStateException("Profile does not exist"));

    if (email != null && email.length() > 0 && !Objects.equals(profile.getEmail(), email)) {
      Optional<Profile> existingProfile = profileRepository.findProfileByEmail(email);
      if (existingProfile.isPresent()) {
        throw new IllegalStateException("Email already exists");
      }
      profile.setEmail(email);
    }
  }

  @Transactional
  public void updatePassword(Long profileId, String oldPassword, String newPassword) {
    Profile profile = profileRepository.findById(profileId)
        .orElseThrow(() -> new IllegalStateException("Profile does not exist"));

    if (!passwordEncoder.matches(oldPassword, profile.getPassword())) {
      throw new IllegalStateException("Incorrect old password");
    }

    String encodedPassword = passwordEncoder.encode(newPassword);
    profile.setPassword(encodedPassword);
  }
}