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
      // Profile profile1 = new Profile("a@a.com",
      // "$2a$10$7bvaS3j/I7leTWcM3XGl4.KNSHnmAkCIQusFsVPH1eujRuH6Ex7/q");

      // Profile profile2 = new Profile("b@b.com", "b");

      // repository.saveAll(List.of(profile1, profile2));
    };

  };
}
