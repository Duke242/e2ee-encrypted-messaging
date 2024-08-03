package com.example.demo.profile;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
}
