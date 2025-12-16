package com.tonepolish.client.retry;

import org.springframework.http.HttpStatusCode;

import java.time.Duration;

public class RetryPolicy {
    private final int maxRetries;
    private final Duration initialDelay;
    private final double backoffMultiplier;
    private final Duration maxDelay;

    public RetryPolicy(int maxRetries, Duration initialDelay, double backoffMultiplier, Duration maxDelay) {
        this.maxRetries = maxRetries;
        this.initialDelay = initialDelay;
        this.backoffMultiplier = backoffMultiplier;
        this.maxDelay = maxDelay;
    }

    public int getMaxRetries() {
        return maxRetries;
    }

    public Duration calculateDelay(int attemptNumber) {
        long delayMs = (long) (initialDelay.toMillis() * Math.pow(backoffMultiplier, attemptNumber - 1));
        long cappedDelay = Math.min(delayMs, maxDelay.toMillis());
        return Duration.ofMillis(cappedDelay);
    }

    public static boolean isRetryableError(Throwable error) {
        if (error instanceof org.springframework.web.reactive.function.client.WebClientResponseException) {
            org.springframework.web.reactive.function.client.WebClientResponseException httpError = 
                (org.springframework.web.reactive.function.client.WebClientResponseException) error;
            
            HttpStatusCode status = httpError.getStatusCode();
            int statusCode = status.value();
            
            // Retry on 5xx server errors
            if (statusCode >= 500 && statusCode < 600) {
                return true;
            }
            
            // Retry on 429 Too Many Requests (rate limit)
            if (statusCode == 429) {
                return true;
            }
            
            // Don't retry on 4xx client errors (except 429)
            if (statusCode >= 400 && statusCode < 500) {
                return false;
            }
        }
        
        // Retry on timeout and network errors
        if (error instanceof java.util.concurrent.TimeoutException) {
            return true;
        }
        
        if (error instanceof java.net.ConnectException || 
            error instanceof java.net.SocketTimeoutException ||
            error instanceof java.io.IOException) {
            return true;
        }
        
        // Don't retry on other exceptions (parsing errors, etc.)
        return false;
    }
}

