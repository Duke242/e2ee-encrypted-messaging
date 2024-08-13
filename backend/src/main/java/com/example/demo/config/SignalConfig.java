package com.example.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.whispersystems.libsignal.IdentityKeyPair;
import org.whispersystems.libsignal.state.SignalProtocolStore;
import org.whispersystems.libsignal.state.impl.InMemorySignalProtocolStore;
import org.whispersystems.libsignal.util.KeyHelper;

@Configuration
public class SignalConfig {

  @Bean
  public SignalProtocolStore signalProtocolStore() {
    IdentityKeyPair identityKeyPair = KeyHelper.generateIdentityKeyPair();
    int registrationId = KeyHelper.generateRegistrationId(false);

    return new InMemorySignalProtocolStore(identityKeyPair, registrationId);
  }
}