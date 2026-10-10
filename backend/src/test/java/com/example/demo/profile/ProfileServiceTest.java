package com.example.demo.profile;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProfileServiceTest {

    @Mock
    private ProfileRepository profileRepository;

    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(4);

    private ProfileService profileService;

    @BeforeEach
    void setUp() {
        profileService = new ProfileService(profileRepository, passwordEncoder);
    }

    @Test
    void addProfileStoresAHashedPasswordAndThePublicKey() {
        when(profileRepository.findProfileByEmail("alice@example.com")).thenReturn(Optional.empty());

        profileService.addProfile(new Profile("alice@example.com", "s3cret-pass", null), "alice-public-key");

        ArgumentCaptor<Profile> saved = ArgumentCaptor.forClass(Profile.class);
        verify(profileRepository).save(saved.capture());
        assertThat(saved.getValue().getPassword()).isNotEqualTo("s3cret-pass");
        assertThat(passwordEncoder.matches("s3cret-pass", saved.getValue().getPassword())).isTrue();
        assertThat(saved.getValue().getPublicKey()).isEqualTo("alice-public-key");
    }

    @Test
    void addProfileRejectsADuplicateEmail() {
        when(profileRepository.findProfileByEmail("alice@example.com"))
                .thenReturn(Optional.of(new Profile("alice@example.com", "x", "k")));

        assertThatThrownBy(() -> profileService.addProfile(new Profile("alice@example.com", "p", null), "k"))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Email already exists");
        verify(profileRepository, never()).save(any());
    }

    @Test
    void updatePasswordRejectsAWrongOldPassword() {
        Profile alice = new Profile(1L, "alice@example.com", passwordEncoder.encode("right-pass"), "k");
        when(profileRepository.findById(1L)).thenReturn(Optional.of(alice));

        assertThatThrownBy(() -> profileService.updatePassword(1L, "wrong-pass", "new-pass"))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Incorrect old password");
        assertThat(passwordEncoder.matches("right-pass", alice.getPassword())).isTrue();
    }

    @Test
    void updatePasswordHashesTheNewPassword() {
        Profile alice = new Profile(1L, "alice@example.com", passwordEncoder.encode("right-pass"), "k");
        when(profileRepository.findById(1L)).thenReturn(Optional.of(alice));

        profileService.updatePassword(1L, "right-pass", "new-pass");

        assertThat(passwordEncoder.matches("new-pass", alice.getPassword())).isTrue();
    }

    @Test
    void updateProfileRejectsAnEmailAlreadyTaken() {
        Profile alice = new Profile(1L, "alice@example.com", "h", "k");
        when(profileRepository.findById(1L)).thenReturn(Optional.of(alice));
        when(profileRepository.findProfileByEmail("bob@example.com"))
                .thenReturn(Optional.of(new Profile(2L, "bob@example.com", "h", "k")));

        assertThatThrownBy(() -> profileService.updateProfile(1L, "bob@example.com"))
                .isInstanceOf(IllegalStateException.class);
        assertThat(alice.getEmail()).isEqualTo("alice@example.com");
    }
}
