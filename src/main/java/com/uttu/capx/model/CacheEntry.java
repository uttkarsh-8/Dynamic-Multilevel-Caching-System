package com.uttu.capx.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CacheEntry<K, V> {
    private K key;
    private V value;
    private long lastAccessed;
    private int accessCount;
}
