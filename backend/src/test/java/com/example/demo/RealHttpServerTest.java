package com.example.demo;

import com.example.demo.message.MessageRepository;
import com.example.demo.profile.ProfileRepository;
import com.fasterxml.jackson.databind.JsonNode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Runs the API on a real embedded server. Unlike MockMvc, this exercises the servlet container's
 * error dispatch and the exact JSON that goes over the wire.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class RealHttpServerTest {

    @Autowired
    private TestRestTemplate http;

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private ProfileRepository profileRepository;

    private record User(long id, String email, String token) {
    }

    @BeforeEach
    void cleanDatabase() {
        messageRepository.deleteAll();
        profileRepository.deleteAll();
    }

    private User signUp(String email) {
        JsonNode body = http.postForObject("/api/auth/signup",
                Map.of("email", email, "password", "correct-horse-battery", "publicKey", email + "-key"),
                JsonNode.class);
        return new User(body.get("id").asLong(), email, body.get("token").asText());
    }

    private HttpEntity<Object> as(User user, Object body) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(user.token());
        return new HttpEntity<>(body, headers);
    }

    @Test
    void forbiddenStays403OverRealHttp() {
        User alice = signUp("alice@example.com");
        User bob = signUp("bob@example.com");

        ResponseEntity<String> response = http.exchange("/api/messages/user/{id}/conversations",
                HttpMethod.GET, as(alice, null), String.class, bob.id());

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    void conversationsNeverExposePasswordHashes() {
        User alice = signUp("alice@example.com");
        User bob = signUp("bob@example.com");
        http.exchange("/api/messages/send", HttpMethod.POST,
                as(alice, Map.of("recipientEmail", bob.email(), "content", "ciphertext")), String.class);

        ResponseEntity<String> response = http.exchange("/api/messages/user/{id}/conversations",
                HttpMethod.GET, as(alice, null), String.class, alice.id());

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).contains("ciphertext");
        assertThat(response.getBody()).doesNotContain("password").doesNotContain("$2a$");
    }
}
