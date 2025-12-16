package com.tonepolish.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tonepolish.client.dto.ChatCompletionRequest;
import com.tonepolish.client.dto.ChatCompletionResponse;
import com.tonepolish.client.exception.AIClientException;
import com.tonepolish.client.prompt.PromptManager;
import com.tonepolish.client.retry.RetryExecutor;
import com.tonepolish.client.retry.RetryPolicy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;

public class GroqAIClient implements AIClient {

    private static final Logger logger = LoggerFactory.getLogger(GroqAIClient.class);

    private final WebClient webClient;
    private final String model;
    private final PromptManager promptManager;
    private final ObjectMapper objectMapper;
    private final RetryExecutor retryExecutor;

    public GroqAIClient(String apiKey, String apiUrl, String model, PromptManager promptManager,
                       int maxRetries, Duration initialDelay, double backoffMultiplier, Duration maxDelay) {
        if (apiUrl == null || apiUrl.isEmpty()) {
            throw new IllegalArgumentException("ai.api.url must be configured");
        }
        if (apiKey == null || apiKey.isEmpty()) {
            throw new IllegalArgumentException("ai.api.key must be configured");
        }
        
        this.model = model;
        this.promptManager = promptManager;
        this.objectMapper = new ObjectMapper();
        
        RetryPolicy retryPolicy = new RetryPolicy(maxRetries, initialDelay, backoffMultiplier, maxDelay);
        this.retryExecutor = new RetryExecutor(retryPolicy);
        
        this.webClient = WebClient.builder()
            .baseUrl(apiUrl)
            .defaultHeader("Content-Type", "application/json")
            .defaultHeader("Authorization", "Bearer " + apiKey)
            .build();
    }

    @Override
    public String refineText(String inputText) {
        if (inputText == null || inputText.trim().isEmpty()) {
            throw new IllegalArgumentException("Input text cannot be empty");
        }

        logger.debug("Calling AI API to refine text, model: {}", model);
        
        ChatCompletionRequest request = buildRequest(inputText);
        String requestBody;
        try {
            requestBody = objectMapper.writeValueAsString(request);
        } catch (Exception e) {
            logger.error("Failed to serialize request", e);
            throw new AIClientException("Failed to serialize request", e);
        }

        try {
            String responseBody = retryExecutor.execute(() -> {
                try {
                    return webClient.post()
                        .bodyValue(requestBody)
                        .retrieve()
                        .bodyToMono(String.class)
                        .timeout(Duration.ofSeconds(30))
                        .doOnError(error -> {
                            if (RetryPolicy.isRetryableError(error)) {
                                logger.warn("Retryable error occurred: {}", error.getMessage());
                            } else {
                                logger.error("Non-retryable error occurred", error);
                            }
                        })
                        .block();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            });

            ChatCompletionResponse response = objectMapper.readValue(responseBody, ChatCompletionResponse.class);

            if (response == null || response.getChoices() == null || response.getChoices().isEmpty()) {
                throw new AIClientException("Empty response from AI API");
            }

            String content = response.getChoices().get(0).getMessage().getContent();
            if (content == null || content.trim().isEmpty()) {
                throw new AIClientException("Empty content in AI API response");
            }

            logger.debug("Successfully refined text");
            return content.trim();

        } catch (WebClientResponseException e) {
            logger.error("HTTP error calling AI API: status={}, body={}", e.getStatusCode(), e.getResponseBodyAsString());
            throw new AIClientException("AI API returned error: " + e.getStatusCode(), e);
        } catch (RuntimeException e) {
            if (e.getCause() instanceof WebClientResponseException) {
                WebClientResponseException httpError = (WebClientResponseException) e.getCause();
                logger.error("HTTP error after retries: status={}, body={}", 
                    httpError.getStatusCode(), httpError.getResponseBodyAsString());
                throw new AIClientException("AI API returned error after retries: " + httpError.getStatusCode(), httpError);
            }
            logger.error("Failed to call AI API after retries", e);
            throw new AIClientException("Failed to call AI API: " + e.getMessage(), e);
        } catch (Exception e) {
            logger.error("Unexpected error calling AI API", e);
            throw new AIClientException("Unexpected error: " + e.getMessage(), e);
        }
    }

    private ChatCompletionRequest buildRequest(String inputText) {
        List<ChatCompletionRequest.Message> messages = Arrays.asList(
            new ChatCompletionRequest.Message("system", promptManager.getSystemPromptForRefinement()),
            new ChatCompletionRequest.Message("user", inputText)
        );
        return new ChatCompletionRequest(model, messages);
    }
}

