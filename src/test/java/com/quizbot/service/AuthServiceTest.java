package com.quizbot.service;

import com.quizbot.enums.Role;
import com.quizbot.helper.CustomOIDCAuthUser;
import com.quizbot.model.User;
import com.quizbot.repo.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;

import java.time.Instant;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {

    private static final String SUB = "google-sub-123";
    private static final String EMAIL = "avishka@example.com";
    private static final String NAME = "Avishka Chamod";
    private static final String PICTURE = "https://testserver.com/avishka.png";

    @Mock
    private UserRepository userRepository;

    private AuthService authService;

    @BeforeEach
    void setUp(){
        authService = new AuthService(userRepository);
    }

    // HELPER: builds a request that does NOT trigger a UserInfo HTTP call
    private OidcUserRequest buildRequest() {

        ClientRegistration  request = ClientRegistration.withRegistrationId("google")
                .clientId("client-id")
                .clientSecret("client-secret")
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                .redirectUri("{baseUrl}/login/oauth2/code/{registrationId}")
                .scope("openid", "profile", "email")
                .authorizationUri("https://accounts.google.com/o/oauth2/v2/auth")
                .tokenUri("https://oauth2.googleapis.com/token")
                .jwkSetUri("https://www.googleapis.com/oauth2/v3/certs")
                .userNameAttributeName("sub")
                .build();

        Instant now = Instant.now();

        OAuth2AccessToken accessToken = new OAuth2AccessToken(
                OAuth2AccessToken.TokenType.BEARER, "access-token",
                now, now.plusSeconds(3600), Set.of("openid", "profile", "email"));

        OidcIdToken idToken = new OidcIdToken("id-token", now, now.plusSeconds(3600), Map.of(
                "sub", SUB,
                "email", EMAIL,
                "name", NAME,
                "picture", PICTURE));

        return new OidcUserRequest(request, accessToken, idToken);

    }

    @Test
    void loadUser_createsNewUser_whenGoogleSubNotFound() {
        when(userRepository.findByGoogleSub(SUB)).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        OidcUser result = authService.loadUser(buildRequest());

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(captor.capture());
        User saved = captor.getValue();

        assertEquals(SUB, saved.getGoogleSub());
        assertEquals(EMAIL, saved.getEmail());
        assertEquals(NAME, saved.getName());
        assertEquals(PICTURE, saved.getPictureUrl());
        assertEquals(Role.USER, saved.getRole());
        assertNotNull(saved.getFirstLogin());
        assertNotNull(saved.getLastLogin());

        assertInstanceOf(CustomOIDCAuthUser.class, result);
    }


    @Test
    void loadUser_updatesExistingUser_whenGoogleSubFound() {
        Instant originalFirstLogin = Instant.parse("2024-01-01T00:00:00Z");
        Instant originalLastLogin = Instant.parse("2024-06-01T00:00:00Z");

        User existing = new User();
        existing.setGoogleSub(SUB);
        existing.setEmail("old@example.com");
        existing.setName("Old Name");
        existing.setPictureUrl("https://example.com/old.png");
        existing.setRole(Role.USER);
        existing.setFirstLogin(originalFirstLogin);
        existing.setLastLogin(originalLastLogin);

        when(userRepository.findByGoogleSub(SUB)).thenReturn(Optional.of(existing));
        when(userRepository.save(existing)).thenReturn(existing);

        OidcUser result = authService.loadUser(buildRequest());

        verify(userRepository).save(existing);

        assertEquals(EMAIL, existing.getEmail());
        assertEquals(NAME, existing.getName());
        assertEquals(PICTURE, existing.getPictureUrl());
        assertTrue(existing.getLastLogin().isAfter(originalLastLogin));
        assertEquals(originalFirstLogin, existing.getFirstLogin()); // must not change

        assertInstanceOf(CustomOIDCAuthUser.class, result);
    }

    @Test
    void loadUser_savesExactlyOnce_andNeverCreatesDuplicate() {
        when(userRepository.findByGoogleSub(SUB)).thenReturn(Optional.of(new User()));
        when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        authService.loadUser(buildRequest());

        verify(userRepository, times(1)).save(any(User.class));
        verify(userRepository, times(1)).findByGoogleSub(SUB);
        verifyNoMoreInteractions(userRepository);
    }


}
