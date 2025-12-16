package com.tonepolish.client;

public class MockAIClient implements AIClient {

    @Override
    public String refineText(String inputText) {
        // Simulate API delay
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // Simple mock transformation
        String polished = inputText.trim();
        
        // Add professional tone
        if (!polished.endsWith(".") && !polished.endsWith("!") && !polished.endsWith("?")) {
            polished += ".";
        }
        
        // Capitalize first letter
        if (polished.length() > 0) {
            polished = polished.substring(0, 1).toUpperCase() + polished.substring(1);
        }

        // Add empathetic prefix if the text seems negative
        String lowerText = inputText.toLowerCase();
        if (lowerText.contains("sorry") || lowerText.contains("apologize") || 
            lowerText.contains("issue") || lowerText.contains("problem")) {
            polished = "I understand your concern. " + polished;
        }

        return polished;
    }
}

