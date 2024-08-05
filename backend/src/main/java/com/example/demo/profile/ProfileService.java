package com.example.demo.profile;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import jakarta.transaction.Transactional;

@Service
public class ProfileService {

  private final ProfileRepository profileRepository;

  @Autowired
  public ProfileService(ProfileRepository profileRepository) {
    this.profileRepository = profileRepository;
  }

  public List<Profile> getProfiles() {
    return profileRepository.findAll();
  }

  public void addProfile(Profile profile) {

    Optional<Profile> profileOptional = profileRepository.findProfileByEmail(profile.getEmail());
    if (profileOptional.isPresent()) {
      throw new IllegalStateException("Email already exists");
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
}
