package com.example.demo.profile;

import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ProfileConfig {

  @Bean
  CommandLineRunner commandLineRunner(ProfileRepository repository) {
    return args -> {
      Profile profile1 = new Profile("John", "john@gmail.com");

      Profile profile2 = new Profile("John", "john@gmail.com");

      repository.saveAll(List.of(profile1, profile2));
    };

  };
}
