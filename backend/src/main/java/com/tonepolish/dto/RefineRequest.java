package com.tonepolish.dto;

public class RefineRequest {
    private String text;

    public RefineRequest() {
    }

    public RefineRequest(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}

