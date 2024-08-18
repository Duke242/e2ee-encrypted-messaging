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
  // Profile profile9 = new Profile("user9@example.com", "password9",
  // "publicKey9");
  // Profile profile10 = new Profile("user10@example.com", "password10",
  // "publicKey10");
  // Profile profile11 = new Profile("user11@example.com", "password11",
  // "publicKey11");
  // Profile profile12 = new Profile("user12@example.com", "password12",
  // "publicKey12");
  // Profile profile13 = new Profile("user13@example.com", "password13",
  // "publicKey13");
  // Profile profile14 = new Profile("user14@example.com", "password14",
  // "publicKey14");
  // Profile profile15 = new Profile("user15@example.com", "password15",
  // "publicKey15");

  // Save profiles to the database
  // profile1 = profileRepository.save(profile1);
  // profile2 = profileRepository.save(profile2);
  // profile3 = profileRepository.save(profile3);
  // profile4 = profileRepository.save(profile4);
  // profile5 = profileRepository.save(profile5);
  // profile6 = profileRepository.save(profile6);
  // profile7 = profileRepository.save(profile7);
  // profile8 = profileRepository.save(profile8);
  // profile9 = profileRepository.save(profile9);
  // profile10 = profileRepository.save(profile10);
  // profile11 = profileRepository.save(profile11);
  // profile12 = profileRepository.save(profile12);
  // profile13 = profileRepository.save(profile13);
  // profile14 = profileRepository.save(profile14);
  // profile15 = profileRepository.save(profile15);

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
  // messageRepository.save(new Message(profile9, profile10, "Profile10, I have
  // some exciting news to share!", "metadata15"));
  // messageRepository.save(new Message(profile10, profile9, "Profile9, can't wait
  // to hear it. What's up?", "metadata16"));

  // messageRepository.save(new Message(profile11, profile12, "Profile12, are you
  // available for a quick chat?", "metadata17"));
  // messageRepository.save(new Message(profile12, profile11, "Profile11, I'm free
  // now. What do you need?", "metadata18"));

  // messageRepository.save(new Message(profile13, profile14, "Profile14, I need
  // your opinion on something.", "metadata19"));
  // messageRepository.save(new Message(profile14, profile13, "Profile13, sure
  // thing. What’s up?", "metadata20"));

  // messageRepository.save(new Message(profile15, profile11, "Profile11, let's
  // discuss our next steps.", "metadata21"));
  // messageRepository.save(new Message(profile11, profile15, "Profile15, I agree.
  // When would be a good time?", "metadata22"));

  // messageRepository.save(new Message(profile12, profile13, "Profile13, do you
  // have any updates on the project?", "metadata23"));
  // messageRepository.save(new Message(profile13, profile12, "Profile12, I’m
  // working on it. I’ll update you soon.", "metadata24"));

  // messageRepository.save(new Message(profile1, profile5, "Profile5, let's
  // discuss our upcoming event.", "metadata37"));
  // messageRepository.save(new Message(profile5, profile1, "Profile1, I’m
  // excited. What do we need to cover?", "metadata38"));

  // messageRepository.save(new Message(profile2, profile6, "Profile6, can you
  // review the report I sent?", "metadata39"));
  // messageRepository.save(new Message(profile6, profile2, "Profile2, I received
  // it. I’ll get back to you by tomorrow.", "metadata40"));

  // messageRepository.save(new Message(profile3, profile7, "Profile7, do you have
  // any feedback on the draft?", "metadata41"));
  // messageRepository.save(new Message(profile7, profile3, "Profile3, I’m still
  // reviewing it but will send comments soon.", "metadata42"));

  // messageRepository.save(new Message(profile4, profile8, "Profile8, have you
  // finished the analysis?", "metadata43"));
  // messageRepository.save(new Message(profile8, profile4, "Profile4, almost
  // done. I’ll update you later today.", "metadata44"));

  // messageRepository.save(new Message(profile5, profile9, "Profile9, I have some
  // updates regarding the budget.", "metadata45"));
  // messageRepository.save(new Message(profile9, profile5, "Profile5, great!
  // Let’s discuss it in detail.", "metadata46"));

  // messageRepository.save(new Message(profile6, profile10, "Profile10, I need
  // your input on the new proposal.", "metadata47"));
  // messageRepository.save(new Message(profile10, profile6, "Profile6, I’ll
  // provide feedback by the end of the day.", "metadata48"));

  // messageRepository.save(new Message(profile7, profile11, "Profile11, do you
  // want to collaborate on the project?", "metadata49"));
  // messageRepository.save(new Message(profile11, profile7, "Profile7,
  // absolutely! Let’s set up a meeting.", "metadata50"));

  // messageRepository.save(new Message(profile8, profile12, "Profile12, can you
  // send over the latest stats?", "metadata51"));
  // messageRepository.save(new Message(profile12, profile8, "Profile8, I’ll get
  // those to you shortly.", "metadata52"));

  // messageRepository.save(new Message(profile9, profile13, "Profile13, we need
  // to finalize the schedule.", "metadata53"));
  // messageRepository.save(new Message(profile13, profile9, "Profile9, I’m
  // working on it. I’ll send an update soon.", "metadata54"));

  // messageRepository.save(new Message(profile10, profile14, "Profile14, have you
  // reviewed the new design?", "metadata55"));
  // messageRepository.save(new Message(profile14, profile10, "Profile10, I have
  // some suggestions. Let’s discuss.", "metadata56"));

  // messageRepository.save(new Message(profile11, profile15, "Profile15, do you
  // have any availability this week?", "metadata57"));
  // messageRepository.save(new Message(profile15, profile11, "Profile11, I’m
  // available on Wednesday. Does that work for you?", "metadata58"));

  // messageRepository.save(new Message(profile12, profile14, "Profile14, please
  // confirm receipt of the documents.", "metadata59"));
  // messageRepository.save(new Message(profile14, profile12, "Profile12, received
  // and reviewed. Everything looks good.", "metadata60"));

  // messageRepository.save(new Message(profile13, profile15, "Profile15, I have
  // some questions about the project.", "metadata61"));
  // messageRepository.save(new Message(profile15, profile13, "Profile13, feel
  // free to ask. I’m here to help.", "metadata62"));

  // messageRepository.save(new Message(profile14, profile1, "Profile1, can we
  // arrange a meeting for next week?", "metadata63"));
  // messageRepository.save(new Message(profile1, profile14, "Profile14, sure!
  // Let’s coordinate a time.", "metadata64"));

  // messageRepository.save(new Message(profile2, profile4, "Profile4, can you
  // provide an update on the metrics?", "metadata65"));
  // messageRepository.save(new Message(profile4, profile2, "Profile2, I’m
  // compiling the data and will send it shortly.", "metadata66"));

  // messageRepository.save(new Message(profile3, profile6, "Profile6, let's
  // review the latest changes together.", "metadata67"));
  // messageRepository.save(new Message(profile6, profile3, "Profile3, I’m free
  // later this afternoon. Let’s sync up then.", "metadata68"));

  // messageRepository.save(new Message(profile7, profile8, "Profile8, I need your
  // approval on the new draft.", "metadata69"));
  // messageRepository.save(new Message(profile8, profile7, "Profile7, I’ll review
  // and get back to you by end of day.", "metadata70"));

  // messageRepository.save(new Message(profile9, profile12, "Profile12, let's
  // discuss the upcoming event planning.", "metadata71"));
  // messageRepository.save(new Message(profile12, profile9, "Profile9, sounds
  // good. What’s the agenda?", "metadata72"));

  // messageRepository.save(new Message(profile10, profile13, "Profile13, any
  // updates on the project timeline?", "metadata73"));
  // messageRepository.save(new Message(profile13, profile10, "Profile10, I’ll
  // have an update for you by tomorrow.", "metadata74"));

  // messageRepository.save(new Message(profile11, profile14, "Profile14, can you
  // send the final version of the report?", "metadata75"));
  // messageRepository.save(new Message(profile14, profile11, "Profile11, I’ll
  // send it over by end of day.", "metadata76"));

  // messageRepository.save(new Message(profile12, profile15, "Profile15, are we
  // still on for the meeting next week?", "metadata77"));
  // messageRepository.save(new Message(profile15, profile12, "Profile12, yes,
  // let’s confirm the time and date.", "metadata78"));

  // messageRepository.save(new Message(profile13, profile8, "Profile8, do you
  // have any updates on our joint task?", "metadata79"));
  // messageRepository.save(new Message(profile8, profile13, "Profile13, I’ll
  // provide an update later today.", "metadata80"));

  // messageRepository.save(new Message(profile14, profile7, "Profile7, let’s
  // review the new changes together.", "metadata81"));
  // messageRepository.save(new Message(profile7, profile14, "Profile14, I’m
  // available this afternoon. Let’s sync up.", "metadata82"));

  // messageRepository.save(new Message(profile15, profile2, "Profile2, any news
  // on the project you’re working on?", "metadata83"));
  // messageRepository.save(new Message(profile2, profile15, "Profile15, I’ll have
  // some updates for you by tomorrow.", "metadata84"));

  // messageRepository.save(new Message(profile1, profile6, "Profile6, can we
  // discuss the new proposal details?", "metadata85"));
  // messageRepository.save(new Message(profile6, profile1, "Profile1, yes, let’s
  // set up a meeting to go over everything.", "metadata86"));

  // messageRepository.save(new Message(profile4, profile5, "Profile5, do you have
  // any thoughts on the latest design?", "metadata87"));
  // messageRepository.save(new Message(profile5, profile4, "Profile4, I think
  // it’s almost there. Let’s make a few tweaks.", "metadata88"));

  // }
}
