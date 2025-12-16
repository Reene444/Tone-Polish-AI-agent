package com.tonepolish.config;

import com.tonepolish.client.AIClient;
import com.tonepolish.client.GroqAIClient;
import com.tonepolish.client.MockAIClient;
import com.tonepolish.client.prompt.PromptManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.time.Duration;

@Configuration
public class AIClientConfig {

    @Autowired
    private PromptManager promptManager;

    @Bean("groqAIClient")
    @ConditionalOnProperty(name = "ai.api.key", matchIfMissing = false)
    public AIClient groqAIClient(@Value("${ai.api.key}") String apiKey,
                                 @Value("${ai.api.url}") String apiUrl,
                                 @Value("${ai.api.model:llama-3.1-8b-instant}") String model,
                                 @Value("${ai.api.retry.max-retries:3}") int maxRetries,
                                 @Value("${ai.api.retry.initial-delay-ms:1000}") long initialDelayMs,
                                 @Value("${ai.api.retry.backoff-multiplier:2.0}") double backoffMultiplier,
                                 @Value("${ai.api.retry.max-delay-ms:10000}") long maxDelayMs) {
        if (apiKey == null || apiKey.isEmpty() || apiUrl == null || apiUrl.isEmpty()) {
            throw new IllegalArgumentException("ai.api.key and ai.api.url must be configured for GroqAIClient");
        }
        return new GroqAIClient(apiKey, apiUrl, model, promptManager,
            maxRetries, 
            Duration.ofMillis(initialDelayMs), 
            backoffMultiplier, 
            Duration.ofMillis(maxDelayMs));
    }

    @Bean("mockAIClient")
    @Primary
    public AIClient mockAIClient() {
        return new MockAIClient();
    }
}

