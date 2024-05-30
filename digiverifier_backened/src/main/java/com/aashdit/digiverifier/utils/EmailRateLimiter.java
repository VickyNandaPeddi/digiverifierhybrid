package com.aashdit.digiverifier.utils;

import com.google.common.util.concurrent.RateLimiter;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

@Component
public class EmailRateLimiter {
    private final ConcurrentHashMap<String, RateLimiter> emailRateLimiters = new ConcurrentHashMap<>();

    public boolean tryAcquire(String email) {
        // You can adjust the rate and capacity as needed
        RateLimiter rateLimiter = emailRateLimiters.computeIfAbsent(email, k -> RateLimiter.create(0.01));

        // Try to acquire a permit, wait for at most 1 second
        return rateLimiter.tryAcquire(1, 1, TimeUnit.SECONDS);
    }
}
