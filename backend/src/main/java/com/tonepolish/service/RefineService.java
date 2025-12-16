package com.tonepolish.service;

import com.tonepolish.client.AIClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class RefineService {

    private final AIClient aiClient;

    @Autowired
    public RefineService(@Value("${ai.api.key:}") String apiKey,
                        @Qualifier("groqAIClient") AIClient groqClient,
                        @Qualifier("mockAIClient") AIClient mockClient) {
        // Use Groq client if API key is provided, otherwise use mock
        if (apiKey != null && !apiKey.isEmpty()) {
            this.aiClient = groqClient;
        } else {
            this.aiClient = mockClient;
        }
    }

    public String refineText(String inputText) {
        return aiClient.refineText(inputText);
    }
}

