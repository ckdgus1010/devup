package com.upstage.devup.auth.config;

public record AuthenticatedUser(
        long userId,
        String role
) {
}
