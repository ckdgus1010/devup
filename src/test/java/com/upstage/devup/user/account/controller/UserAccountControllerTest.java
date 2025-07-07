package com.upstage.devup.user.account.controller;

import com.upstage.devup.auth.config.SecurityConfig;
import com.upstage.devup.auth.config.jwt.JwtTokenProvider;
import com.upstage.devup.global.exception.EntityNotFoundException;
import com.upstage.devup.user.account.dto.UserAccountDto;
import com.upstage.devup.user.account.service.UserAccountService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static com.upstage.devup.Util.getAuthentication;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserAccountController.class)
@Import(SecurityConfig.class)
class UserAccountControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private JwtTokenProvider jwtTokenProvider;

    @MockitoBean
    private UserAccountService userAccountService;

    private static final String ROLE_USER = "ROLE_USER";
    private static final String urlTemplate = "/api/user/account";

    @Test
    @DisplayName("사용자 계정 정보 조회 성공")
    public void shouldReturnUserAccount_whenAuthenticatedRequest() throws Exception {
        // given
        Long userId = 1L;

        UserAccountDto mockDto = UserAccountDto.builder()
                .userId(userId)
                .loginId("dev123")
                .nickname("개발자123")
                .email("dev123@gmail.com")
                .build();

        when(userAccountService.getUserAccount(eq(userId)))
                .thenReturn(mockDto);

        // when & then
        mockMvc.perform(get(urlTemplate)
                        .with(getAuthentication(userId, ROLE_USER)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.userId").value(userId.toString()))
                .andExpect(jsonPath("$.loginId").value(mockDto.getLoginId()))
                .andExpect(jsonPath("$.nickname").value(mockDto.getNickname()))
                .andExpect(jsonPath("$.email").value(mockDto.getEmail()));
    }

    @Test
    @DisplayName("사용자 계정 정보 조회 실패 - 인증되지 않은 접근인 경우")
    public void shouldReturnUnauthorized_whenRequestIsNotAuthenticated() throws Exception {
        mockMvc.perform(get(urlTemplate))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("사용자 계정 정보 조회 실패 - 유효하지 않은 사용자 ID를 사용한 경우")
    public void shouldThrowNotFound_whenUserIdIsUnavailable() throws Exception {
        // given
        Long userId = 0L;
        final String errorMessage = "회원 정보를 찾을 수 없습니다.";

        when(userAccountService.getUserAccount(eq(userId)))
                .thenThrow(new EntityNotFoundException(errorMessage));

        // when & then
        mockMvc.perform(get(urlTemplate)
                        .with(getAuthentication(userId, ROLE_USER)))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value(errorMessage));
    }

}