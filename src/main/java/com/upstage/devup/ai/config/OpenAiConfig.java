package com.upstage.devup.ai.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

import java.nio.charset.StandardCharsets;

@Configuration
public class OpenAiConfig {

    @Value("classpath:/ai/defaultSystem.txt")
    private Resource resource;

    @Bean
    public ChatClient chatClient(ChatClient.Builder chatClientBuilder) {
        return chatClientBuilder
                .defaultSystem(resource, StandardCharsets.UTF_8)
                .build();
    }
}
