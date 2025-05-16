package com.upstage.devup.web;

import com.upstage.devup.auth.config.AuthenticatedUser;
import com.upstage.devup.auth.config.SecurityConfig;
import com.upstage.devup.auth.config.jwt.JwtTokenProvider;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@WebMvcTest(AuthViewController.class)
@Import(SecurityConfig.class)
class AuthViewControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private JwtTokenProvider jwtTokenProvider;

    private static final String URI_TEMPLATE = "/auth/signin";

    @Test
    @DisplayName("로그인 페이지 로드 성공")
    public void shouldReturnSignInPage() throws Exception {
        // given

        // when & then
        mockMvc.perform(get(URI_TEMPLATE))
                .andExpect(status().isOk())
                .andExpect(view().name("auth/signin"));
    }

    @Test
    @DisplayName("로그인한 상태에서 로그인 페이지 접속 시 마이페이지로 리다이렉트")
    public void shouldRedirectToMyPage_whenAlreadySignedIn() throws Exception {
        // given
        Long userId = 1L;

        // when & then
        mockMvc.perform(get(URI_TEMPLATE)
                        .with(getAuthentication(userId)))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/mypage"));
    }

    private RequestPostProcessor getAuthentication(Long userId) {
        AuthenticatedUser user = new AuthenticatedUser(userId);
        Authentication auth = new UsernamePasswordAuthenticationToken(user, null, null);
        return authentication(auth);
    }
}