package com.uttu.capx.eviction.impl;

import com.uttu.capx.eviction.EvictionPolicy;
import com.uttu.capx.model.CacheEntry;

import java.util.LinkedHashMap;
import java.util.Map;

public class LRUEvictionPolicy<K, V> implements EvictionPolicy<K, V> {
    private final Map<K, CacheEntry<K, V>> store;

    public LRUEvictionPolicy(int capacity) {
        this.store = new LinkedHashMap<>(capacity, 0.75f, true) {
            @Override
            protected boolean removeEldestEntry(Map.Entry<K, CacheEntry<K, V>> eldest) {
                return size() > capacity;
            }
        };
    }

    @Override
    public void add(K key, CacheEntry<K, V> entry) {
        store.put(key, entry);
    }

    @Override
    public void remove(K key) {
        store.remove(key);
    }

    @Override
    public void update(K key, CacheEntry<K, V> entry) {
        store.put(key, entry);
    }

    @Override
    public K evict() {
        Map.Entry<K, CacheEntry<K, V>> eldest = store.entrySet().iterator().next();
        K key = eldest.getKey();
        store.remove(key);
        return key;
    }
}