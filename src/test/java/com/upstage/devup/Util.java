package com.upstage.devup;

import com.upstage.devup.auth.config.AuthenticatedUser;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

import java.util.List;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;

public class Util {

    private Util() {}

    public static RequestPostProcessor getAuthentication(long userId, String role) {
        AuthenticatedUser user = new AuthenticatedUser(userId, role);

        return authentication(new UsernamePasswordAuthenticationToken(
                user,
                null,
                List.of(new SimpleGrantedAuthority(user.role()))
        ));
    }
}
