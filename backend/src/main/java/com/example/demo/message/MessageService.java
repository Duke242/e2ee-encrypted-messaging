package com.example.demo.message;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.whispersystems.libsignal.DuplicateMessageException;
import org.whispersystems.libsignal.InvalidKeyIdException;
import org.whispersystems.libsignal.InvalidMessageException;
import org.whispersystems.libsignal.LegacyMessageException;
import org.whispersystems.libsignal.NoSessionException;
import org.whispersystems.libsignal.SignalProtocolAddress;
import org.whispersystems.libsignal.protocol.CiphertextMessage;
import org.whispersystems.libsignal.state.SignedPreKeyRecord;

import com.example.demo.profile.Profile;
import com.example.demo.profile.ProfileRepository;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class MessageService {

  private final MessageRepository messageRepository;
  private final ProfileRepository profileRepository;

  private final MessageConfig messageConfig;

  private static final Logger logger = LoggerFactory.getLogger(MessageConfig.class);

  @Autowired
  public MessageService(MessageRepository messageRepository, ProfileRepository profileRepository,
      MessageConfig messageConfig) {
    this.messageRepository = messageRepository;
    this.profileRepository = profileRepository;
    this.messageConfig = messageConfig;

  }
}