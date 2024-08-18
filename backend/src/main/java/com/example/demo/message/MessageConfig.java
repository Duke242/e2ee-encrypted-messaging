package com.example.demo.message;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import com.example.demo.profile.Profile;
import com.example.demo.profile.ProfileRepository;
import java.time.LocalDateTime;

@Configuration
public class MessageConfig {

  @Autowired
  private MessageRepository messageRepository;

  @Autowired
  private ProfileRepository profileRepository;

  // Create profiles
  // Profile profile1 = new Profile("user1@example.com", "password1",
  // "publicKey1");
  // Profile profile2 = new Profile("user2@example.com", "password2",
  // "publicKey2");
  // Profile profile3 = new Profile("user3@example.com", "password3",
  // "publicKey3");
  // Profile profile4 = new Profile("user4@example.com", "password4",
  // "publicKey4");
  // Profile profile5 = new Profile("user5@example.com", "password5",
  // "publicKey5");
  // Profile profile6 = new Profile("user6@example.com", "password6",
  // "publicKey6");
  // Profile profile7 = new Profile("user7@example.com", "password7",
  // "publicKey7");
  // Profile profile8 = new Profile("user8@example.com", "password8",
  // "publicKey8");

  // Save profiles to the database
  // profile1 = profileRepository.save(profile1);
  // profile2 = profileRepository.save(profile2);
  // profile3 = profileRepository.save(profile3);
  // profile4 = profileRepository.save(profile4);
  // profile5 = profileRepository.save(profile5);
  // profile6 = profileRepository.save(profile6);
  // profile7 = profileRepository.save(profile7);
  // profile8 = profileRepository.save(profile8);

  // messageRepository.save(new Message(profile1, profile2, "Hi profile2, this is
  // profile1. How are you?", "metadata1"));
  // messageRepository.save(new Message(profile2, profile1, "Hi profile1! I'm
  // doing well, thanks. How about you?", "metadata2"));
  // messageRepository.save(new Message(profile3, profile4, "Profile4, this is
  // profile3. Let's catch up soon.", "metadata3"));
  // messageRepository.save(new Message(profile4, profile3, "Profile3, great to
  // hear from you. I'm free this weekend.", "metadata4"));
  // messageRepository.save(new Message(profile5, profile6, "Profile6, I have some
  // updates for you.", "metadata5"));
  // messageRepository.save(new Message(profile6, profile5, "Profile5, looking
  // forward to your updates.", "metadata6"));
  // messageRepository.save(new Message(profile7, profile8, "Profile8, can we
  // schedule a meeting?", "metadata7"));
  // messageRepository.save(new Message(profile8, profile7, "Profile7, sure! When
  // works for you?", "metadata8"));

  // messageRepository.save(new Message(profile1, profile3, "Hello profile3, do
  // you have any news?", "metadata9"));
  // messageRepository.save(new Message(profile2, profile4, "Profile4, I just
  // wanted to check in.", "metadata10"));
  // messageRepository.save(new Message(profile5, profile7, "Profile7, are you
  // available for a call?", "metadata11"));
  // messageRepository.save(new Message(profile6, profile8, "Profile8, let's
  // discuss the project.", "metadata12"));
  // messageRepository.save(new Message(profile7, profile1, "Profile1, let's catch
  // up about the latest changes.", "metadata13"));
  // messageRepository.save(new Message(profile8, profile2, "Profile2, hope
  // everything is going well with you.", "metadata14"));
  // }
}
