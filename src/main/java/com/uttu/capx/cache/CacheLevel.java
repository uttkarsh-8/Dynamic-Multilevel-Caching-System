package com.uttu.capx.cache;

import com.uttu.capx.model.CacheEntry;

import java.util.Map;

public interface CacheLevel<K, V> {
    V get(K key);
    void put(K key, V value);
    void evict(K key);
    int size();
    boolean isEmpty();
    void clear();
    Map<K, CacheEntry<K, V>> getAll();
}