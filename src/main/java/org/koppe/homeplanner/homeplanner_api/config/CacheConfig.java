package org.koppe.homeplanner.homeplanner_api.config;

import java.util.concurrent.TimeUnit;

import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.github.benmanes.caffeine.cache.Caffeine;

@Configuration
public class CacheConfig {

    @Bean
    public CacheManager cacheManager() {
        CaffeineCacheManager cm = new CaffeineCacheManager();
        cm.setCaffeine(Caffeine.newBuilder().maximumSize(1000).expireAfterWrite(15, TimeUnit.MINUTES).recordStats());
        return cm;
    }
}
