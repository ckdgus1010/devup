package com.upstage.devup.web;

import com.upstage.devup.auth.config.SecurityConfig;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@Import(SecurityConfig.class)
@WebMvcTest(HomeController.class)
class HomeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("홈 화면 요청 시 index.html 반환")
    public void shouldReturnIndex() throws Exception {
        // given
        String urlTemplate = "/";
        String expectedViewName = "index";

        // when & then
        mockMvc.perform(get(urlTemplate))
                .andExpect(status().isOk())
                .andExpect(view().name(expectedViewName));
    }
}