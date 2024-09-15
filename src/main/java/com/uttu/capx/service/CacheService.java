package com.uttu.capx.service;

import com.uttu.capx.cache.CacheLevel;
import com.uttu.capx.cache.MultiLevelCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CacheService {

    private final MultiLevelCache<String, Object> multiLevelCache;

    @Autowired
    public CacheService(MultiLevelCache<String, Object> multiLevelCache) {
        this.multiLevelCache = multiLevelCache;
    }

    public Object get(String key) {
        return multiLevelCache.get(key);
    }

    public void put(String key, Object value) {
        multiLevelCache.put(key, value);
    }

    public void evict(String key) {
        multiLevelCache.evict(key);
    }

    public void clear() {
        multiLevelCache.clear();
    }

    public void addCacheLevel(CacheLevel<String, Object> cacheLevel, int index) {
        multiLevelCache.addCacheLevel(cacheLevel, index);
    }

    public void removeCacheLevel(int index) {
        multiLevelCache.removeCacheLevel(index);
    }

    public int getCacheLevelCount() {
        return multiLevelCache.getLevelCount();
    }

    public String getCacheStatus() {
        return multiLevelCache.toString();
    }
}