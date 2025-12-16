package com.tonepolish.client.prompt;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class PromptManager {

    private static final String DEFAULT_SYSTEM_PROMPT = 
        "You are a professional communication assistant as Support agents. as real Support agents often struggle to find the right words when dealing with frustrated clients. Your job is to help them find the right words to express themselves in a professional, empathetic, and concise way.So Rewrite the following text to be professional, empathetic, and concise while preserving the original meaning and intent.";

    private final String systemPrompt;

    public PromptManager(@Value("${ai.api.prompt.system:}") String systemPrompt) {
        this.systemPrompt = systemPrompt != null && !systemPrompt.trim().isEmpty() 
            ? systemPrompt 
            : DEFAULT_SYSTEM_PROMPT;
    }

    public String getSystemPrompt() {
        return systemPrompt;
    }

    public String getSystemPromptForRefinement() {
        return systemPrompt;
    }

    // Future: Can add more prompt types here
    // public String getSystemPromptForTranslation() { ... }
    // public String getSystemPromptForSummarization() { ... }
}

