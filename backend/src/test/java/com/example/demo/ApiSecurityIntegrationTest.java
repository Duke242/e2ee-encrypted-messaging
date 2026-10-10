package com.example.demo;

import com.example.demo.message.Message;
import com.example.demo.message.MessageRepository;
import com.example.demo.profile.ProfileRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Drives the real HTTP API (Spring Security filter chain, controllers, JPA) against an in-memory database.
 * Each test signs users up through the API and acts with the JWT the API hands back.
 */
@SpringBootTest
@AutoConfigureMockMvc
class ApiSecurityIntegrationTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper json;

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private ProfileRepository profileRepository;

    private record User(long id, String email, String token) {
        String bearer() {
            return "Bearer " + token;
        }
    }

    @BeforeEach
    void cleanDatabase() {
        messageRepository.deleteAll();
        profileRepository.deleteAll();
    }

    private User signUp(String email) throws Exception {
        String body = json.writeValueAsString(Map.of(
                "email", email, "password", "correct-horse-battery", "publicKey", email + "-public-key"));
        String response = mvc.perform(post("/api/auth/signup").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        JsonNode node = json.readTree(response);
        return new User(node.get("id").asLong(), email, node.get("token").asText());
    }

    private void sendAs(User author, long claimedSenderId, String recipientEmail) throws Exception {
        String body = json.writeValueAsString(Map.of(
                "senderId", claimedSenderId, "recipientEmail", recipientEmail, "content", "ciphertext"));
        mvc.perform(post("/api/messages/send").header("Authorization", author.bearer())
                        .contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isOk());
    }

    // --- Authentication ---

    @Test
    void healthCheckIsPublic() throws Exception {
        mvc.perform(get("/actuator/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("UP"));
    }

    @Test
    void requestWithoutATokenGets401() throws Exception {
        mvc.perform(get("/api/messages/user/1/conversations"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void requestWithAForgedTokenGets401() throws Exception {
        mvc.perform(get("/api/profiles").header("Authorization", "Bearer not.a.real.token"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void signInWithTheWrongPasswordGets401() throws Exception {
        signUp("alice@example.com");
        String body = json.writeValueAsString(Map.of("email", "alice@example.com", "password", "wrong-password"));

        mvc.perform(post("/api/auth/signin").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void signInWithTheRightPasswordReturnsAToken() throws Exception {
        signUp("alice@example.com");
        String body = json.writeValueAsString(Map.of("email", "alice@example.com", "password", "correct-horse-battery"));

        mvc.perform(post("/api/auth/signin").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isNotEmpty());
    }

    // --- Authorization: users may only act on their own data ---

    @Test
    void userCanReadTheirOwnConversations() throws Exception {
        User alice = signUp("alice@example.com");
        User bob = signUp("bob@example.com");
        sendAs(alice, alice.id(), bob.email());

        mvc.perform(get("/api/messages/user/{id}/conversations", alice.id()).header("Authorization", alice.bearer()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$." + bob.id()).isArray());
    }

    @Test
    void userCannotReadSomeoneElsesConversations() throws Exception {
        User alice = signUp("alice@example.com");
        User bob = signUp("bob@example.com");
        User carol = signUp("carol@example.com");
        sendAs(bob, bob.id(), carol.email());

        mvc.perform(get("/api/messages/user/{id}/conversations", bob.id()).header("Authorization", alice.bearer()))
                .andExpect(status().isForbidden());
    }

    @Test
    void senderIsTheAuthenticatedUserNotTheIdInTheRequestBody() throws Exception {
        User alice = signUp("alice@example.com");
        User bob = signUp("bob@example.com");
        User carol = signUp("carol@example.com");

        // Alice claims to be Bob in the request body.
        sendAs(alice, bob.id(), carol.email());

        List<Message> stored = messageRepository.findAll();
        assertThat(stored).hasSize(1);
        assertThat(stored.get(0).getSenderId()).isEqualTo(alice.id());
    }

    @Test
    void userCannotReplaceSomeoneElsesPublicKey() throws Exception {
        User alice = signUp("alice@example.com");
        User bob = signUp("bob@example.com");

        mvc.perform(put("/api/profiles/{id}/public-key", bob.id()).header("Authorization", alice.bearer())
                        .content("attacker-controlled-key"))
                .andExpect(status().isForbidden());

        assertThat(profileRepository.findById(bob.id()).orElseThrow().getPublicKey())
                .isEqualTo("bob@example.com-public-key");
    }

    @Test
    void userCanReplaceTheirOwnPublicKey() throws Exception {
        User alice = signUp("alice@example.com");

        mvc.perform(put("/api/profiles/{id}/public-key", alice.id()).header("Authorization", alice.bearer())
                        .content("rotated-key"))
                .andExpect(status().isOk());

        assertThat(profileRepository.findById(alice.id()).orElseThrow().getPublicKey()).isEqualTo("rotated-key");
    }

    @Test
    void userCannotDeleteOrEditSomeoneElsesProfile() throws Exception {
        User alice = signUp("alice@example.com");
        User bob = signUp("bob@example.com");

        mvc.perform(delete("/api/profiles/{id}", bob.id()).header("Authorization", alice.bearer()))
                .andExpect(status().isForbidden());
        mvc.perform(put("/api/profiles/{id}", bob.id()).param("email", "stolen@example.com")
                        .header("Authorization", alice.bearer()))
                .andExpect(status().isForbidden());

        assertThat(profileRepository.findById(bob.id()).orElseThrow().getEmail()).isEqualTo("bob@example.com");
    }
}
