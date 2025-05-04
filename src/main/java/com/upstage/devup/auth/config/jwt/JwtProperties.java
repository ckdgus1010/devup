package com.upstage.devup.auth.config.jwt;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties("jwt")
@Getter
@Setter
public class JwtProperties {

    private String secretKey;
    private String issuer;
    private long expiration;

}
