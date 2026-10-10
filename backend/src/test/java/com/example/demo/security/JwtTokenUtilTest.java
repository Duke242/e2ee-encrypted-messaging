package com.example.demo.security;

import com.example.demo.profile.Profile;
import com.example.demo.profile.ProfileRepository;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Date;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JwtTokenUtilTest {

    private static final String SECRET =
            "dGVzdC1vbmx5LWp3dC1zZWNyZXQtZm9yLWp1bml0LXRlc3RzLW5vdC1mb3ItcHJvZHVjdGlvbi11c2UtMDEyMzQ1Njc4OQ==";
    private static final String OTHER_SECRET =
            "YW5vdGhlci1rZXktdGhhdC10aGUtc2VydmVyLWRvZXMtbm90LXRydXN0LWF0LWFsbC1mb3ItYW55LXRva2VuLTAxMjM0NTY3";

    @Mock
    private ProfileRepository profileRepository;

    private JwtTokenUtil jwtTokenUtil;

    @BeforeEach
    void setUp() {
        jwtTokenUtil = new JwtTokenUtil();
        ReflectionTestUtils.setField(jwtTokenUtil, "profileRepository", profileRepository);
        ReflectionTestUtils.setField(jwtTokenUtil, "jwtSecret", SECRET);
        ReflectionTestUtils.setField(jwtTokenUtil, "jwtExpirationMs", 60_000);
    }

    private UsernamePasswordAuthenticationToken authFor(Profile profile) {
        return new UsernamePasswordAuthenticationToken(profile, null, profile.getAuthorities());
    }

    @Test
    void issuedTokenValidatesAndCarriesTheEmail() {
        Profile alice = new Profile(7L, "alice@example.com", "hash", "key");
        when(profileRepository.findProfileByEmail("alice@example.com")).thenReturn(Optional.of(alice));

        String token = jwtTokenUtil.generateJwtToken(authFor(alice));

        assertThat(jwtTokenUtil.validateJwtToken(token)).isTrue();
        assertThat(jwtTokenUtil.getUserNameFromJwtToken(token)).isEqualTo("alice@example.com");
    }

    @Test
    void tokenSignedWithAnotherKeyIsRejected() {
        String forged = Jwts.builder()
                .setSubject("alice@example.com")
                .setExpiration(new Date(System.currentTimeMillis() + 60_000))
                .signWith(SignatureAlgorithm.HS512, OTHER_SECRET)
                .compact();

        assertThat(jwtTokenUtil.validateJwtToken(forged)).isFalse();
    }

    @Test
    void expiredTokenIsRejected() {
        String expired = Jwts.builder()
                .setSubject("alice@example.com")
                .setIssuedAt(new Date(System.currentTimeMillis() - 120_000))
                .setExpiration(new Date(System.currentTimeMillis() - 60_000))
                .signWith(SignatureAlgorithm.HS512, SECRET)
                .compact();

        assertThat(jwtTokenUtil.validateJwtToken(expired)).isFalse();
    }

    @Test
    void malformedAndEmptyTokensAreRejected() {
        assertThat(jwtTokenUtil.validateJwtToken("not.a.jwt")).isFalse();
        assertThat(jwtTokenUtil.validateJwtToken("")).isFalse();
    }

    @Test
    void cannotIssueATokenForAnUnknownUser() {
        Profile ghost = new Profile(99L, "ghost@example.com", "hash", "key");
        when(profileRepository.findProfileByEmail("ghost@example.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> jwtTokenUtil.generateJwtToken(authFor(ghost)))
                .isInstanceOf(UsernameNotFoundException.class);
    }
}
