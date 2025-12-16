package com.tonepolish.dto;

public class RefineResponse {
    private String polishedText;

    public RefineResponse() {
    }

    public RefineResponse(String polishedText) {
        this.polishedText = polishedText;
    }

    public String getPolishedText() {
        return polishedText;
    }

    public void setPolishedText(String polishedText) {
        this.polishedText = polishedText;
    }
}

