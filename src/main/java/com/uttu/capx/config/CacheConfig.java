package com.uttu.capx.config;

import com.uttu.capx.cache.CacheLevel;
import com.uttu.capx.cache.MultiLevelCache;
import com.uttu.capx.cache.impl.LFUCache;
import com.uttu.capx.cache.impl.LRUCache;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CacheConfig {

    @Bean
    public MultiLevelCache<String, Object> multiLevelCache() {
        MultiLevelCache<String, Object> cache = new MultiLevelCache<>();

        // Configure cache levels
        cache.addCacheLevel(new LRUCache<>(100), 0); // L1 cache: LRU with 100 entries
        cache.addCacheLevel(new LFUCache<>(500), 1); // L2 cache: LFU with 500 entries

        return cache;
    }

    @Bean
    public CacheLevel<String, Object> l1Cache() {
        return new LRUCache<>(100);
    }

    @Bean
    public CacheLevel<String, Object> l2Cache() {
        return new LFUCache<>(500);
    }
}