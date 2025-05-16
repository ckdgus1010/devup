package com.upstage.devup.global.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "cookie") // application.yml에서 cookie.* 값을 주입받음
@Getter
@Setter
public class CookieProperties {

    /**
     * HTTPS에서만 전송할지 여부(설정 값)
     */
    private boolean secure;

    /**
     * 쿠키 유효 기간(초 단위, 설정 값)
     */
    private long accessTokenMaxAge;

}
