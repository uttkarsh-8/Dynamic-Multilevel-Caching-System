package com.uttu.capx.eviction.impl;

import com.uttu.capx.eviction.EvictionPolicy;
import com.uttu.capx.model.CacheEntry;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;

public class LFUEvictionPolicy<K, V> implements EvictionPolicy<K, V> {
    private final Map<K, CacheEntry<K, V>> cache;
    private final Map<K, Integer> frequencies;
    private final Map<Integer, LinkedHashSet<K>> frequencyList;
    private int minFrequency;
    private final int capacity;

    public LFUEvictionPolicy(int capacity) {
        this.cache = new HashMap<>();
        this.frequencies = new HashMap<>();
        this.frequencyList = new HashMap<>();
        this.minFrequency = 0;
        this.capacity = capacity;
    }

    @Override
    public void add(K key, CacheEntry<K, V> entry) {
        if (cache.size() >= capacity) {
            evict();
        }
        cache.put(key, entry);
        addFrequency(key);
    }

    @Override
    public void remove(K key) {
        if (cache.containsKey(key)) {
            int frequency = frequencies.get(key);
            frequencies.remove(key);
            frequencyList.get(frequency).remove(key);
            if (frequencyList.get(frequency).isEmpty()) {
                frequencyList.remove(frequency);
                if (minFrequency == frequency) {
                    minFrequency++;
                }
            }
            cache.remove(key);
        }
    }

    @Override
    public void update(K key, CacheEntry<K, V> entry) {
        if (cache.containsKey(key)) {
            cache.put(key, entry);
            addFrequency(key);
        }
    }

    @Override
    public K evict() {
        if (cache.isEmpty()) return null;

        LinkedHashSet<K> minFrequencyKeys = frequencyList.get(minFrequency);
        K evictKey = minFrequencyKeys.iterator().next();
        minFrequencyKeys.remove(evictKey);
        if (minFrequencyKeys.isEmpty()) {
            frequencyList.remove(minFrequency);
        }
        frequencies.remove(evictKey);
        cache.remove(evictKey);
        return evictKey;
    }

    private void addFrequency(K key) {
        int frequency = frequencies.getOrDefault(key, 0);
        frequencies.put(key, frequency + 1);
        frequencyList.computeIfAbsent(frequency + 1, k -> new LinkedHashSet<>()).add(key);
        if (frequency > 0) {
            frequencyList.get(frequency).remove(key);
            if (frequencyList.get(frequency).isEmpty()) {
                frequencyList.remove(frequency);
                if (minFrequency == frequency) {
                    minFrequency++;
                }
            }
        } else {
            minFrequency = 1;
        }
    }
}