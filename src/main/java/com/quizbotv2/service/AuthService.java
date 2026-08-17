package com.quizbotv2.service;

import com.quizbotv2.dto.userdtos.UserDTO;
import com.quizbotv2.model.User;
import com.quizbotv2.repo.UserRepository;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class AuthService extends OidcUserService {

    private final UserRepository userRepository;

    public AuthService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public OidcUser loadUser(OidcUserRequest userRequest) throws OAuth2AuthenticationException {
        OidcUser oidcUser = super.loadUser(userRequest);

        String sub = oidcUser.getSubject();
        String email = oidcUser.getEmail();
        String name = oidcUser.getFullName();
        String picture = oidcUser.getPicture();

        userRepository.findByGoogleSub(sub)
                .map(existing -> {
                    existing.setEmail(email);
                    existing.setName(name);
                    existing.setPictureUrl(picture);
                    existing.setLastLogin(Instant.now());
                    return userRepository.save(existing);
                })
                .orElseGet(() -> {
                    User user = new User();
                    user.setGoogleSub(sub);
                    user.setEmail(email);
                    user.setPictureUrl(picture);
                    user.setName(name);
                    user.setFirstLogin(Instant.now());
                    user.setLastLogin(Instant.now());
                    return userRepository.save(user);
                });
        return oidcUser;
    }
}
