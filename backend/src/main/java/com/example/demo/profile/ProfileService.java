package com.example.demo.profile;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import jakarta.transaction.Transactional;

@Service
public class ProfileService {

  private final ProfileRepository profileRepository;
  private final PasswordEncoder passwordEncoder;

  @Autowired
  public ProfileService(ProfileRepository profileRepository, PasswordEncoder passwordEncoder) {
    this.profileRepository = profileRepository;
    this.passwordEncoder = passwordEncoder;
  }

  public List<Profile> getProfiles() {
    return profileRepository.findAll();
  }

  public List<String> getProfileEmails() {
    List<Profile> profiles = profileRepository.findAll();
    return profiles.stream()
        .map(Profile::getEmail)
        .collect(Collectors.toList());
  }

  public void addProfile(Profile profile) {
    Optional<Profile> profileOptional = profileRepository.findProfileByEmail(profile.getEmail());
    if (profileOptional.isPresent()) {
      throw new IllegalStateException("Email already exists");
    }

    String encodedPassword = passwordEncoder.encode(profile.getPassword());
    profile.setPassword(encodedPassword);

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