package com.example.demo.profile;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "api/profiles")
public class ProfileController {

  private final ProfileService profileService;

  @Autowired
  public ProfileController(ProfileService profileService) {
    this.profileService = profileService;
  }

  @GetMapping("/emails")
  public List<String> getProfileEmails() {
    return profileService.getProfileEmails();
  }

  @GetMapping
  public List<Profile> getProfiles() {
    return profileService.getProfiles();
  }

  @PostMapping
  public void addNewProfile(@RequestBody Profile profile) {
    profileService.addProfile(profile);
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