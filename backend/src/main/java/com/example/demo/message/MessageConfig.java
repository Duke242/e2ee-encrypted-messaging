package com.example.demo.message;

import com.example.demo.profile.Profile;
import com.example.demo.profile.ProfileRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class MessageConfig {

  @Bean
  CommandLineRunner messageCommandLineRunner(MessageRepository messageRepository, ProfileRepository profileRepository) {
    return args -> {

      Profile profile1 = profileRepository.findProfileByEmail("a@a.com")
          .orElseGet(() -> profileRepository
              .save(new Profile("a@a.com",
                  "$2a$10$33vRlc7Em5A5XGLHuvyD0.UMM1AODwVu7N5t5UcfGNKYTOGMREL2i")));
      Profile profile2 = profileRepository.findProfileByEmail("b@b.com")
          .orElseGet(() -> profileRepository
              .save(new Profile("b@b.com",
                  "$2a$10$HjJSZZtYW/BYu8fByi8ypegaQsKPSpQBKmuiM3FsvjsTDgDQOU9se")));

      Message message1 = new Message(profile1, profile2, "Hello, how are you?", false);
      Message message2 = new Message(profile2, profile1, "I'm doing well, thank you!", false);
      Message message3 = new Message(profile1, profile2, "This is not an encrypted message.", false);
      Message message4 = new Message(profile2, profile1, "How was your day?", false);
      Message message5 = new Message(profile1, profile2, "It was great, thanks for asking.", false);
      Message message6 = new Message(profile2, profile1, "I'm looking forward to the weekend.", false);
      Message message7 = new Message(profile1, profile2, "Me too, I need a break.", false);
      Message message8 = new Message(profile2, profile1, "Do you have any plans?", false);
      Message message9 = new Message(profile1, profile2, "Not yet, but I'm thinking of going to the beach.", false);
      Message message10 = new Message(profile2, profile1, "That sounds like fun. I might join you.", false);

      messageRepository.saveAll(
          List.of(message1, message2, message3, message4, message5, message6, message7, message8, message9, message10));
    };
  }
}