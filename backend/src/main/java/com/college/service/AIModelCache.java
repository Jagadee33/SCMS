package com.college.service;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@EnableCaching
@Slf4j
public class AIModelCache {
    
    private final Map<String, Object> cache = new ConcurrentHashMap<>();
    private final Map<String, Long> lastUpdated = new ConcurrentHashMap<>();
    
    @Cacheable(value = "studentPredictions", key = "#studentId")
    public Object getCachedPrediction(Long studentId) {
        String key = "prediction_" + studentId;
        if (cache.containsKey(key)) {
            log.debug("Cache hit for student prediction: {}", studentId);
            return cache.get(key);
        }
        return null;
    }
    
    public void putCachedPrediction(Long studentId, Object prediction) {
        String key = "prediction_" + studentId;
        cache.put(key, prediction);
        lastUpdated.put(key, System.currentTimeMillis());
        log.debug("Cached prediction for student: {}", studentId);
    }
    
    @Cacheable(value = "riskAssessments", key = "#studentId")
    public Object getCachedRiskAssessment(Long studentId) {
        String key = "risk_" + studentId;
        if (cache.containsKey(key)) {
            log.debug("Cache hit for risk assessment: {}", studentId);
            return cache.get(key);
        }
        return null;
    }
    
    public void putCachedRiskAssessment(Long studentId, Object assessment) {
        String key = "risk_" + studentId;
        cache.put(key, assessment);
        lastUpdated.put(key, System.currentTimeMillis());
        log.debug("Cached risk assessment for student: {}", studentId);
    }
    
    public boolean isCacheFresh(Long studentId, long maxAgeMinutes) {
        String key = "prediction_" + studentId;
        Long lastTime = lastUpdated.get(key);
        if (lastTime == null) return false;
        
        long ageMinutes = (System.currentTimeMillis() - lastTime) / (60 * 1000);
        return ageMinutes < maxAgeMinutes;
    }
    
    public void clearCache() {
        cache.clear();
        lastUpdated.clear();
        log.info("AI model cache cleared");
    }
    
    public Map<String, Object> getCacheStats() {
        return Map.of(
            "totalCachedItems", cache.size(),
            "cacheKeys", cache.keySet(),
            "memoryUsage", cache.size() * 100 // Estimated bytes per entry
        );
    }
}
