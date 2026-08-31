package com.quizbotv2.helper;

import com.quizbotv2.model.User;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.OidcUserInfo;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;

import java.util.Set;

public class CustomOIDCAuthUser extends DefaultOidcUser {

    public CustomOIDCAuthUser(User user, OidcIdToken idToken, OidcUserInfo userInfo){
        super(Set.of(new SimpleGrantedAuthority("ROLE_" + user.getRole().name())),
         idToken, userInfo);
    }

}
