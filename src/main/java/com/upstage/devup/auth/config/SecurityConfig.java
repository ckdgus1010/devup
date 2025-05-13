package com.upstage.devup.auth.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    public static final String[] STATIC_RESOURCES = {
            "/css/**", "/js/**", "/images/**"
    };

    public static final String[] PUBLIC_PAGES = {
            "/",
            "/auth/signup", "/auth/signin",
            "/questions/**"
    };

    public static final String[] PUBLIC_APIS = {
            "/api/answers/**",
            "/api/auth/signin"
    };

    @Value("${security.csrf-enabled:true}")
    private boolean csrfEnabled;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        if (!csrfEnabled) {
//            http.csrf(csrf -> csrf.disable());
            http.csrf(AbstractHttpConfigurer::disable);
        }
        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(STATIC_RESOURCES).permitAll()
                        .requestMatchers(PUBLIC_PAGES).permitAll()
                        .requestMatchers(PUBLIC_APIS).permitAll()
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/auth/signin")
                        .permitAll()
                ).logout(Customizer.withDefaults());

        return http.build();
    }

}
