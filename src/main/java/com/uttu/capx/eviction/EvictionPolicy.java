package com.uttu.capx.eviction;

import com.uttu.capx.model.CacheEntry;

public interface EvictionPolicy<K, V> {
    void add(K key, CacheEntry<K, V> entry);
    void remove(K key);
    void update(K key, CacheEntry<K, V> entry);
    K evict();
}