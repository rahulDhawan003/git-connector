package com.github.connector.exception;

public class RateLimitExceededException extends RuntimeException{
    private final long retryAfterMinutes;


    public RateLimitExceededException(String msg, long retryAfterMins) {
        super(msg);
        this.retryAfterMinutes = retryAfterMins;
    }

    public long getRetryAfterMinutes() {
        return retryAfterMinutes;
    }
}
