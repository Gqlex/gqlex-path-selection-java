package com.intuit.gqlex.security;

import java.time.LocalDateTime;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Rate limiter for GraphQL queries with support for multiple time windows.
 * Provides per-minute, per-hour, and per-day rate limiting capabilities.
 */
public class RateLimiter {
    
    // Rate limit configuration
    private int maxQueriesPerMinute = 1000;
    private int maxQueriesPerHour = 10000;
    private int maxQueriesPerDay = 100000;
    
    // Rate limit tracking
    private final ConcurrentHashMap<String, UserRateLimit> userLimits = new ConcurrentHashMap<>();
    
    /**
     * Creates a rate limiter with default limits.
     */
    public RateLimiter() {
    }
    
    /**
     * Creates a rate limiter with custom limits.
     * 
     * @param maxQueriesPerMinute maximum queries per minute
     * @param maxQueriesPerHour maximum queries per hour
     * @param maxQueriesPerDay maximum queries per day
     */
    public RateLimiter(int maxQueriesPerMinute, int maxQueriesPerHour, int maxQueriesPerDay) {
        this.maxQueriesPerMinute = maxQueriesPerMinute;
        this.maxQueriesPerHour = maxQueriesPerHour;
        this.maxQueriesPerDay = maxQueriesPerDay;
    }
    
    /**
     * Checks if a user is allowed to make a query based on rate limits.
     * 
     * @param userId the user ID to check
     * @return rate limit result
     */
    public RateLimitResult checkRateLimit(String userId) {
        if (userId == null || userId.trim().isEmpty()) {
            return RateLimitResult.allowed("Anonymous user");
        }
        
        UserRateLimit userLimit = userLimits.computeIfAbsent(userId, k -> new UserRateLimit());
        LocalDateTime now = LocalDateTime.now();
        
        // Check minute limit
        if (!userLimit.checkMinuteLimit(now, maxQueriesPerMinute)) {
            return RateLimitResult.exceeded(RateLimitType.PER_MINUTE, 
                userLimit.getMinuteCount(), maxQueriesPerMinute);
        }
        
        // Check hour limit
        if (!userLimit.checkHourLimit(now, maxQueriesPerHour)) {
            return RateLimitResult.exceeded(RateLimitType.PER_HOUR, 
                userLimit.getHourCount(), maxQueriesPerHour);
        }
        
        // Check day limit
        if (!userLimit.checkDayLimit(now, maxQueriesPerDay)) {
            return RateLimitResult.exceeded(RateLimitType.PER_DAY, 
                userLimit.getDayCount(), maxQueriesPerDay);
        }
        
        // All checks passed, increment counters
        userLimit.incrementCounts(now);
        
        return RateLimitResult.allowed("Rate limit check passed");
    }
    
    /**
     * Gets the current rate limit status for a user.
     * 
     * @param userId the user ID to check
     * @return rate limit status
     */
    public RateLimitStatus getRateLimitStatus(String userId) {
        if (userId == null || userId.trim().isEmpty()) {
            return new RateLimitStatus();
        }
        
        UserRateLimit userLimit = userLimits.get(userId);
        if (userLimit == null) {
            return new RateLimitStatus();
        }
        
        LocalDateTime now = LocalDateTime.now();
        RateLimitStatus status = new RateLimitStatus();
        
        status.setMinuteCount(userLimit.getMinuteCount());
        status.setMinuteLimit(maxQueriesPerMinute);
        status.setMinuteRemaining(maxQueriesPerMinute - userLimit.getMinuteCount());
        status.setMinuteResetTime(userLimit.getMinuteResetTime());
        
        status.setHourCount(userLimit.getHourCount());
        status.setHourLimit(maxQueriesPerHour);
        status.setHourRemaining(maxQueriesPerHour - userLimit.getHourCount());
        status.setHourResetTime(userLimit.getHourResetTime());
        
        status.setDayCount(userLimit.getDayCount());
        status.setDayLimit(maxQueriesPerDay);
        status.setDayRemaining(maxQueriesPerDay - userLimit.getDayCount());
        status.setDayResetTime(userLimit.getDayResetTime());
        
        return status;
    }
    
    /**
     * Resets rate limits for a specific user.
     * 
     * @param userId the user ID to reset
     */
    public void resetUserLimits(String userId) {
        if (userId != null) {
            userLimits.remove(userId);
        }
    }
    
    /**
     * Resets all rate limits.
     */
    public void resetAllLimits() {
        userLimits.clear();
    }
    
    /**
     * Gets the number of active users with rate limits.
     * 
     * @return number of active users
     */
    public int getActiveUserCount() {
        return userLimits.size();
    }
    
    /**
     * Sets the maximum queries per minute.
     */
    public RateLimiter setMaxQueriesPerMinute(int maxQueriesPerMinute) {
        this.maxQueriesPerMinute = maxQueriesPerMinute;
        return this;
    }
    
    /**
     * Sets the maximum queries per hour.
     */
    public RateLimiter setMaxQueriesPerHour(int maxQueriesPerHour) {
        this.maxQueriesPerHour = maxQueriesPerHour;
        return this;
    }
    
    /**
     * Sets the maximum queries per day.
     */
    public RateLimiter setMaxQueriesPerDay(int maxQueriesPerDay) {
        this.maxQueriesPerDay = maxQueriesPerDay;
        return this;
    }
    
    /**
     * Gets the maximum queries per minute.
     */
    public int getMaxQueriesPerMinute() {
        return maxQueriesPerMinute;
    }
    
    /**
     * Gets the maximum queries per hour.
     */
    public int getMaxQueriesPerHour() {
        return maxQueriesPerHour;
    }
    
    /**
     * Gets the maximum queries per day.
     */
    public int getMaxQueriesPerDay() {
        return maxQueriesPerDay;
    }
    
    /**
     * Inner class to track rate limits for a single user.
     */
    private static class UserRateLimit {
        private final AtomicInteger minuteCount = new AtomicInteger(0);
        private final AtomicInteger hourCount = new AtomicInteger(0);
        private final AtomicInteger dayCount = new AtomicInteger(0);
        
        private volatile LocalDateTime minuteResetTime;
        private volatile LocalDateTime hourResetTime;
        private volatile LocalDateTime dayResetTime;
        
        public boolean checkMinuteLimit(LocalDateTime now, int limit) {
            if (minuteResetTime == null || now.isAfter(minuteResetTime)) {
                minuteCount.set(0);
                minuteResetTime = now.plusMinutes(1);
            }
            return minuteCount.get() < limit;
        }
        
        public boolean checkHourLimit(LocalDateTime now, int limit) {
            if (hourResetTime == null || now.isAfter(hourResetTime)) {
                hourCount.set(0);
                hourResetTime = now.plusHours(1);
            }
            return hourCount.get() < limit;
        }
        
        public boolean checkDayLimit(LocalDateTime now, int limit) {
            if (dayResetTime == null || now.isAfter(dayResetTime)) {
                dayCount.set(0);
                dayResetTime = now.plusDays(1);
            }
            return dayCount.get() < limit;
        }
        
        public void incrementCounts(LocalDateTime now) {
            // Check and reset minute counter
            if (minuteResetTime == null || now.isAfter(minuteResetTime)) {
                minuteCount.set(0);
                minuteResetTime = now.plusMinutes(1);
            }
            minuteCount.incrementAndGet();
            
            // Check and reset hour counter
            if (hourResetTime == null || now.isAfter(hourResetTime)) {
                hourCount.set(0);
                hourResetTime = now.plusHours(1);
            }
            hourCount.incrementAndGet();
            
            // Check and reset day counter
            if (dayResetTime == null || now.isAfter(dayResetTime)) {
                dayCount.set(0);
                dayResetTime = now.plusDays(1);
            }
            dayCount.incrementAndGet();
        }
        
        public int getMinuteCount() {
            return minuteCount.get();
        }
        
        public int getHourCount() {
            return hourCount.get();
        }
        
        public int getDayCount() {
            return dayCount.get();
        }
        
        public LocalDateTime getMinuteResetTime() {
            return minuteResetTime;
        }
        
        public LocalDateTime getHourResetTime() {
            return hourResetTime;
        }
        
        public LocalDateTime getDayResetTime() {
            return dayResetTime;
        }
    }
    
    /**
     * Result of a rate limit check.
     */
    public static class RateLimitResult {
        private final boolean allowed;
        private final String message;
        private final RateLimitType exceededType;
        private final int currentCount;
        private final int limit;
        
        private RateLimitResult(boolean allowed, String message, RateLimitType exceededType, int currentCount, int limit) {
            this.allowed = allowed;
            this.message = message;
            this.exceededType = exceededType;
            this.currentCount = currentCount;
            this.limit = limit;
        }
        
        public static RateLimitResult allowed(String message) {
            return new RateLimitResult(true, message, null, 0, 0);
        }
        
        public static RateLimitResult exceeded(RateLimitType type, int currentCount, int limit) {
            return new RateLimitResult(false, 
                String.format("Rate limit exceeded for %s (current: %d, limit: %d)", type, currentCount, limit),
                type, currentCount, limit);
        }
        
        public boolean isAllowed() {
            return allowed;
        }
        
        public String getMessage() {
            return message;
        }
        
        public RateLimitType getExceededType() {
            return exceededType;
        }
        
        public int getCurrentCount() {
            return currentCount;
        }
        
        public int getLimit() {
            return limit;
        }
    }
    
    /**
     * Status of rate limits for a user.
     */
    public static class RateLimitStatus {
        private int minuteCount;
        private int minuteLimit;
        private int minuteRemaining;
        private LocalDateTime minuteResetTime;
        
        private int hourCount;
        private int hourLimit;
        private int hourRemaining;
        private LocalDateTime hourResetTime;
        
        private int dayCount;
        private int dayLimit;
        private int dayRemaining;
        private LocalDateTime dayResetTime;
        
        // Getters and setters
        public int getMinuteCount() { return minuteCount; }
        public void setMinuteCount(int minuteCount) { this.minuteCount = minuteCount; }
        
        public int getMinuteLimit() { return minuteLimit; }
        public void setMinuteLimit(int minuteLimit) { this.minuteLimit = minuteLimit; }
        
        public int getMinuteRemaining() { return minuteRemaining; }
        public void setMinuteRemaining(int minuteRemaining) { this.minuteRemaining = minuteRemaining; }
        
        public LocalDateTime getMinuteResetTime() { return minuteResetTime; }
        public void setMinuteResetTime(LocalDateTime minuteResetTime) { this.minuteResetTime = minuteResetTime; }
        
        public int getHourCount() { return hourCount; }
        public void setHourCount(int hourCount) { this.hourCount = hourCount; }
        
        public int getHourLimit() { return hourLimit; }
        public void setHourLimit(int hourLimit) { this.hourLimit = hourLimit; }
        
        public int getHourRemaining() { return hourRemaining; }
        public void setHourRemaining(int hourRemaining) { this.hourRemaining = hourRemaining; }
        
        public LocalDateTime getHourResetTime() { return hourResetTime; }
        public void setHourResetTime(LocalDateTime hourResetTime) { this.hourResetTime = hourResetTime; }
        
        public int getDayCount() { return dayCount; }
        public void setDayCount(int dayCount) { this.dayCount = dayCount; }
        
        public int getDayLimit() { return dayLimit; }
        public void setDayLimit(int dayLimit) { this.dayLimit = dayLimit; }
        
        public int getDayRemaining() { return dayRemaining; }
        public void setDayRemaining(int dayRemaining) { this.dayRemaining = dayRemaining; }
        
        public LocalDateTime getDayResetTime() { return dayResetTime; }
        public void setDayResetTime(LocalDateTime dayResetTime) { this.dayResetTime = dayResetTime; }
    }
    
    /**
     * Type of rate limit.
     */
    public enum RateLimitType {
        PER_MINUTE, PER_HOUR, PER_DAY
    }
} 