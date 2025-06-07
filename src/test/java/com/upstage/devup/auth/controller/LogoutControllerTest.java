package com.upstage.devup.auth.controller;

import com.upstage.devup.auth.config.SecurityConfig;
import com.upstage.devup.auth.config.jwt.JwtTokenProvider;
import com.upstage.devup.global.properties.CookieProperties;
import com.upstage.devup.global.provider.CookieProvider;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;

import static com.upstage.devup.Util.getAuthentication;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.anonymous;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(LogoutController.class)
@AutoConfigureMockMvc
@Import({SecurityConfig.class, CookieProvider.class, CookieProperties.class})
class LogoutControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private JwtTokenProvider jwtTokenProvider;

    private static final String ROLE_USER = "ROLE_USER";
    private static final String URI_TEMPLATE = "/api/auth/logout";

    @Test
    @DisplayName("로그아웃 성공")
    public void shouldReturnRedirectUrlAndClearCookie_whenValidRequest() throws Exception {
        // given
        Long userId = 1L;

        // when & then
        mockMvc.perform(post(URI_TEMPLATE)
                        .with(getAuthentication(userId, ROLE_USER)))
                .andExpect(status().isOk())
                .andExpect(compareStringInHeader("accessToken="))
                .andExpect(compareStringInHeader("Max-Age=0"))
                .andExpect(compareStringInHeader("SameSite=Strict"))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.redirectUrl").value("/"));
    }

    @Test
    @DisplayName("로그아웃 실패 - 로그인하지 않은 경우")
    public void shouldReturnUnauthenticated_whenUserIdIsNull() throws Exception {
        // given

        // when & then
        mockMvc.perform(post(URI_TEMPLATE)
                        .with(anonymous()))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("로그아웃 실패 - 유효하지 않은 사용자 ID를 사용한 경우")
    public void shouldReturnUnauthenticated_whenUserIdIsUnavailable() throws Exception {
        // given
        Long userId = -1L;

        // when & then
        mockMvc.perform(post(URI_TEMPLATE)
                        .with(getAuthentication(userId, ROLE_USER)))
                .andExpect(status().isUnauthorized());
    }

    private ResultMatcher compareStringInHeader(String subString) {
        return header()
                .string(
                        HttpHeaders.SET_COOKIE, Matchers.containsString(subString)
                );
    }

}