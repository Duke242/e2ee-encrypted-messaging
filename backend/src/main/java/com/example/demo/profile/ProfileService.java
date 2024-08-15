package com.example.demo.profile;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

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

  public Optional<List<String>> getProfileEmails() {
    logger.info("Retrieving list of profile emails");
    return profileRepository.findAllEmails();
  }

  public void addProfile(Profile profile, String publicKey) {
    Optional<Profile> profileOptional = profileRepository.findProfileByEmail(profile.getEmail());
    if (profileOptional.isPresent()) {
      throw new IllegalStateException("Email already exists");
    }

    String encodedPassword = passwordEncoder.encode(profile.getPassword());
    profile.setPassword(encodedPassword);
    profile.setPublicKey(publicKey);

    profileRepository.save(profile);
  }

  public void deleteProfile(Long profileId) {
    boolean exists = profileRepository.existsById(profileId);
    if (!exists) {
      throw new IllegalStateException("Profile with id " + profileId + " does not exist");
    }
    profileRepository.deleteById(profileId);
  }

  @Transactional
  public void updateProfile(Long profileId, String email) {
    Profile profile = profileRepository.findById(profileId)
        .orElseThrow(() -> new IllegalStateException("Profile with id " + profileId + " does not exist"));

    if (email != null && email.length() > 0 && !Objects.equals(profile.getEmail(), email)) {
      Optional<Profile> profileOptional = profileRepository.findProfileByEmail(email);
      if (profileOptional.isPresent()) {
        throw new IllegalStateException("Email already taken");
      }
      profile.setEmail(email);
    }
  }

  @Transactional
  public void updatePassword(Long profileId, String oldPassword, String newPassword) {
    Profile profile = profileRepository.findById(profileId)
        .orElseThrow(() -> new IllegalStateException("Profile with id " + profileId + " does not exist"));

    if (!passwordEncoder.matches(oldPassword, profile.getPassword())) {
      throw new IllegalStateException("Incorrect old password");
    }

    String encodedPassword = passwordEncoder.encode(newPassword);
    profile.setPassword(encodedPassword);
  }

  @Transactional
  public void updatePublicKey(Long profileId, String newPublicKey) {
    Profile profile = profileRepository.findById(profileId)
        .orElseThrow(() -> new IllegalStateException("Profile with id " + profileId + " does not exist"));

    profile.setPublicKey(newPublicKey);
  }

  public String getPublicKey(Long profileId) {
    Profile profile = profileRepository.findById(profileId)
        .orElseThrow(() -> new IllegalStateException("Profile with id " + profileId + " does not exist"));

    return profile.getPublicKey();
  }

  private ProfileDTO convertToDTO(Profile profile) {
    return new ProfileDTO(profile.getId(), profile.getEmail(),
        profile.getPublicKey());
  }
}