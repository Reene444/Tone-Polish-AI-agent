package com.tonepolish.service;

import com.tonepolish.client.AIClient;
import com.tonepolish.client.MockAIClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RefineServiceTest {

    private RefineService refineService;

    @BeforeEach
    void setUp() {
        // Create service with mock client
        AIClient mockClient = new MockAIClient();
        refineService = new RefineService("", mockClient, mockClient);
    }

    @Test
    void testRefineText_WithEmptyInput() {
        String result = refineService.refineText("");
        assertNotNull(result);
        assertFalse(result.isEmpty());
    }

    @Test
    void testRefineText_WithNormalInput() {
        String input = "this is a test message";
        String result = refineService.refineText(input);
        
        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertNotEquals(input, result); // Should be transformed
    }

    @Test
    void testRefineText_WithApologeticInput() {
        String input = "sorry for the issue";
        String result = refineService.refineText(input);
        
        assertNotNull(result);
        assertTrue(result.contains("I understand your concern") || 
                   result.toLowerCase().contains("understand"));
    }

    @Test
    void testRefineText_PreservesMeaning() {
        String input = "The system is down";
        String result = refineService.refineText(input);
        
        assertNotNull(result);
        // Should contain key words from original
        assertTrue(result.toLowerCase().contains("system") || 
                   result.toLowerCase().contains("down"));
    }
}

