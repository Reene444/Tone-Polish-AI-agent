package com.tonepolish.client.retry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.function.Supplier;

public class RetryExecutor {
    private static final Logger logger = LoggerFactory.getLogger(RetryExecutor.class);
    
    private final RetryPolicy retryPolicy;

    public RetryExecutor(RetryPolicy retryPolicy) {
        this.retryPolicy = retryPolicy;
    }

    public <T> T execute(Supplier<T> operation) {
        int attempt = 0;
        Exception lastException = null;

        while (attempt <= retryPolicy.getMaxRetries()) {
            try {
                if (attempt > 0) {
                    Duration delay = retryPolicy.calculateDelay(attempt);
                    logger.info("Retrying after {}ms (attempt {}/{})", 
                        delay.toMillis(), attempt, retryPolicy.getMaxRetries());
                    Thread.sleep(delay.toMillis());
                }

                logger.debug("Executing operation (attempt {}/{})", 
                    attempt + 1, retryPolicy.getMaxRetries() + 1);
                
                return operation.get();

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException("Operation interrupted", e);
            } catch (Exception e) {
                lastException = e;
                attempt++;

                if (attempt > retryPolicy.getMaxRetries()) {
                    logger.error("Max retries ({}) exceeded", retryPolicy.getMaxRetries());
                    break;
                }

                if (!RetryPolicy.isRetryableError(e)) {
                    logger.warn("Non-retryable error encountered: {}", e.getClass().getSimpleName());
                    throw new RuntimeException("Non-retryable error: " + e.getMessage(), e);
                }

                logger.warn("Retryable error on attempt {}: {}", attempt, e.getMessage());
            }
        }

        throw new RuntimeException("Operation failed after " + retryPolicy.getMaxRetries() + " retries", 
            lastException);
    }
}

