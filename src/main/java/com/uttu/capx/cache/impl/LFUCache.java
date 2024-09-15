package com.uttu.capx.cache.impl;

import com.uttu.capx.cache.CacheLevel;
import com.uttu.capx.eviction.EvictionPolicy;
import com.uttu.capx.eviction.impl.LFUEvictionPolicy;
import com.uttu.capx.model.CacheEntry;

import java.util.HashMap;
import java.util.Map;

public class LFUCache<K, V> implements CacheLevel<K, V> {
    private final int capacity;
    private final Map<K, CacheEntry<K, V>> cache;
    private EvictionPolicy<K, V> evictionPolicy;

    public LFUCache(int capacity) {
        this.capacity = capacity;
        this.cache = new HashMap<>(capacity);
        this.evictionPolicy = new LFUEvictionPolicy<>(capacity);
    }

    @Override
    public V get(K key) {
        CacheEntry<K, V> entry = cache.get(key);
        if (entry != null) {
            entry.setLastAccessed(System.currentTimeMillis());
            entry.setAccessCount(entry.getAccessCount() + 1);
            evictionPolicy.update(key, entry);
            return entry.getValue();
        }
        return null;
    }

    @Override
    public void put(K key, V value) {
        if (cache.size() >= capacity && !cache.containsKey(key)) {
            K evictedKey = evictionPolicy.evict();
            cache.remove(evictedKey);
        }
        CacheEntry<K, V> entry = new CacheEntry<>(key, value, System.currentTimeMillis(), 1);
        cache.put(key, entry);
        evictionPolicy.add(key, entry);
    }

    @Override
    public void evict(K key) {
        cache.remove(key);
        evictionPolicy.remove(key);
    }

    @Override
    public int size() {
        return cache.size();
    }

    @Override
    public boolean isEmpty() {
        return cache.isEmpty();
    }

    @Override
    public void clear() {
        cache.clear();
        // Reinitialize the eviction policy
        this.evictionPolicy = new LFUEvictionPolicy<>(capacity);
    }

    @Override
    public Map<K, CacheEntry<K, V>> getAll() {
        return new HashMap<>(cache);
    }
}