package com.example.demo.service;

import com.example.demo.profile.Profile;
import com.example.demo.profile.ProfileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

  @Autowired
  private ProfileRepository profileRepository;

  @Override
  public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
    Profile profile = profileRepository.findProfileByEmail(email)
        .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + email));

    return profile;
  }
}