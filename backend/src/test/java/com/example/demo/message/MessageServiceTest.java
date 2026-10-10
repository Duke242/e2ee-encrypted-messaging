package com.example.demo.message;

import com.example.demo.profile.Profile;
import com.example.demo.profile.ProfileRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MessageServiceTest {

    @Mock
    private MessageRepository messageRepository;

    @Mock
    private ProfileRepository profileRepository;

    private MessageService messageService;

    private final Profile alice = new Profile(1L, "alice@example.com", "h", "ka");
    private final Profile bob = new Profile(2L, "bob@example.com", "h", "kb");
    private final Profile carol = new Profile(3L, "carol@example.com", "h", "kc");

    @BeforeEach
    void setUp() {
        messageService = new MessageService(messageRepository, profileRepository);
    }

    private Message message(Profile from, Profile to, String content, LocalDateTime at) {
        Message m = new Message();
        m.setSender(from);
        m.setRecipient(to);
        m.setContent(content);
        m.setTimestamp(at);
        return m;
    }

    @Test
    void conversationsAreGroupedByTheOtherPersonAndSortedOldestFirst() {
        LocalDateTime t = LocalDateTime.of(2026, 1, 1, 12, 0);
        Message late = message(bob, alice, "late", t.plusMinutes(5));
        Message early = message(alice, bob, "early", t);
        Message fromCarol = message(carol, alice, "hi", t.plusMinutes(1));
        when(messageRepository.findAllByUserIdInvolved(1L)).thenReturn(List.of(late, fromCarol, early));

        Map<Long, List<Message>> conversations = messageService.getAllUserConversations(1L);

        assertThat(conversations).containsOnlyKeys(2L, 3L);
        assertThat(conversations.get(2L)).extracting(Message::getContent).containsExactly("early", "late");
        assertThat(conversations.get(3L)).extracting(Message::getContent).containsExactly("hi");
    }

    @Test
    void sendMessageStoresSenderRecipientAndCiphertext() {
        when(profileRepository.findById(1L)).thenReturn(Optional.of(alice));
        when(profileRepository.findByEmail("bob@example.com")).thenReturn(Optional.of(bob));

        messageService.sendMessage(1L, "bob@example.com", "ciphertext");

        ArgumentCaptor<Message> saved = ArgumentCaptor.forClass(Message.class);
        verify(messageRepository).save(saved.capture());
        assertThat(saved.getValue().getSenderId()).isEqualTo(1L);
        assertThat(saved.getValue().getRecipientId()).isEqualTo(2L);
        assertThat(saved.getValue().getContent()).isEqualTo("ciphertext");
    }

    @Test
    void sendMessageToAnUnknownRecipientFailsAndStoresNothing() {
        when(profileRepository.findById(1L)).thenReturn(Optional.of(alice));
        when(profileRepository.findByEmail("nobody@example.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> messageService.sendMessage(1L, "nobody@example.com", "x"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("recipient");
        verify(messageRepository, never()).save(any());
    }
}
