package com.quizbotv2.helper;

import com.quizbotv2.model.User;
import org.jspecify.annotations.Nullable;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.OidcUserInfo;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;

import java.util.Set;

public class CustomOIDCAuthUser extends DefaultOidcUser {

    private final User user;

    public CustomOIDCAuthUser(
            User user,
            OidcIdToken idToken,
            @Nullable OidcUserInfo userInfo
    ) {
        super(
                Set.of(
                        new SimpleGrantedAuthority(
                                "ROLE_" + user.getRole().name()
                        )
                ),
                idToken,
                userInfo
        );

        this.user = user;
    }

    public User getUser() {
        return user;
    }
}