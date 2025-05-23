package com.upstage.devup.auth.config;

import com.upstage.devup.auth.config.jwt.JwtTokenProvider;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    public static final String[] STATIC_RESOURCES = {
            "/css/**", "/js/**", "/images/**",
            "/favicon.ico"
    };

    public static final String[] PUBLIC_PAGES = {
            "/",
            "/auth/signup", "/auth/signin",
            "/questions/**"
    };

    public static final String[] PUBLIC_APIS = {
            "/api/questions/search",
            "/api/answers/**",
            "/api/auth/signup",
            "/api/auth/signin",
    };

    public static final String[] SWAGGER_API = {
            "/swagger-ui.html",
            "/swagger-ui/**",
            "/v3/api-docs.yaml",
            "/v3/api-docs/**",
            "/swagger-resources/**",
            "/webjars/**"
    };


    @Value("${security.csrf-enabled:true}")
    private boolean csrfEnabled;

    private final JwtTokenProvider jwtTokenProvider;

    public SecurityConfig(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        if (!csrfEnabled) {
            http.csrf(AbstractHttpConfigurer::disable);
        }

        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(STATIC_RESOURCES).permitAll()
                        .requestMatchers(PUBLIC_PAGES).permitAll()
                        .requestMatchers(PUBLIC_APIS).permitAll()
                        .requestMatchers(SWAGGER_API).permitAll()
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/auth/signin")
                        .permitAll()
                ).logout(AbstractHttpConfigurer::disable);

        http
                .addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider), UsernamePasswordAuthenticationFilter.class)
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint(
                                (req, res, ex1) ->
                            res.setStatus(HttpServletResponse.SC_UNAUTHORIZED)
                        )
                );

        return http.build();
    }

}
