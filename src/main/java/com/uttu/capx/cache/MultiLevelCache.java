package com.uttu.capx.cache;

import com.uttu.capx.model.CacheEntry;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class MultiLevelCache<K, V> {
    private final List<CacheLevel<K, V>> cacheLevels;
    private final ReadWriteLock lock;

    public MultiLevelCache() {
        this.cacheLevels = new ArrayList<>();
        this.lock = new ReentrantReadWriteLock();
    }

    // Phase 2.2: Cache Level Management

    public void addCacheLevel(CacheLevel<K, V> cacheLevel, int index) {
        lock.writeLock().lock();
        try {
            if (index < 0 || index > cacheLevels.size()) {
                throw new IndexOutOfBoundsException("Invalid cache level index");
            }
            cacheLevels.add(index, cacheLevel);
            redistributeDataOnAdd(index);
        } finally {
            lock.writeLock().unlock();
        }
    }

    public void removeCacheLevel(int index) {
        lock.writeLock().lock();
        try {
            if (index < 0 || index >= cacheLevels.size()) {
                throw new IndexOutOfBoundsException("Invalid cache level index");
            }
            CacheLevel<K, V> removedLevel = cacheLevels.remove(index);
            redistributeDataOnRemove(removedLevel, index);
        } finally {
            lock.writeLock().unlock();
        }
    }

    private void redistributeDataOnAdd(int newLevelIndex) {
        if (newLevelIndex == cacheLevels.size() - 1) {
            // If it's the last level, no need to redistribute
            return;
        }

        CacheLevel<K, V> newLevel = cacheLevels.get(newLevelIndex);
        CacheLevel<K, V> nextLevel = cacheLevels.get(newLevelIndex + 1);

        Map<K, CacheEntry<K, V>> entries = nextLevel.getAll();
        for (Map.Entry<K, CacheEntry<K, V>> entry : entries.entrySet()) {
            newLevel.put(entry.getKey(), entry.getValue().getValue());
            // Optionally, you might want to remove these entries from the next level
            // nextLevel.evict(entry.getKey());
        }
    }

    private void redistributeDataOnRemove(CacheLevel<K, V> removedLevel, int removedIndex) {
        if (cacheLevels.isEmpty()) {
            // If it was the last level, no need to redistribute
            return;
        }

        int targetIndex = Math.min(removedIndex, cacheLevels.size() - 1);
        CacheLevel<K, V> targetLevel = cacheLevels.get(targetIndex);

        Map<K, CacheEntry<K, V>> entries = removedLevel.getAll();
        for (Map.Entry<K, CacheEntry<K, V>> entry : entries.entrySet()) {
            targetLevel.put(entry.getKey(), entry.getValue().getValue());
        }
    }

    // Phase 2.3: Data Retrieval and Insertion Logic

    public V get(K key) {
        lock.readLock().lock();
        try {
            for (int i = 0; i < cacheLevels.size(); i++) {
                V value = cacheLevels.get(i).get(key);
                if (value != null) {
                    // Found in this level, promote to higher levels
                    promoteToHigherLevels(key, value, i);
                    return value;
                }
            }
            return null; // Not found in any level
        } finally {
            lock.readLock().unlock();
        }
    }

    public void put(K key, V value) {
        lock.writeLock().lock();
        try {
            if (!cacheLevels.isEmpty()) {
                // Insert into the highest priority cache (level 0)
                cacheLevels.get(0).put(key, value);

                // Optionally, you might want to update or remove this key from lower levels
                for (int i = 1; i < cacheLevels.size(); i++) {
                    cacheLevels.get(i).evict(key);
                }
            }
        } finally {
            lock.writeLock().unlock();
        }
    }

    private void promoteToHigherLevels(K key, V value, int foundAtLevel) {
        for (int i = foundAtLevel - 1; i >= 0; i--) {
            cacheLevels.get(i).put(key, value);
        }
    }

    public void evict(K key) {
        lock.writeLock().lock();
        try {
            for (CacheLevel<K, V> cacheLevel : cacheLevels) {
                cacheLevel.evict(key);
            }
        } finally {
            lock.writeLock().unlock();
        }
    }

    public void clear() {
        lock.writeLock().lock();
        try {
            for (CacheLevel<K, V> cacheLevel : cacheLevels) {
                cacheLevel.clear();
            }
        } finally {
            lock.writeLock().unlock();
        }
    }

    // Utility methods

    public int getLevelCount() {
        lock.readLock().lock();
        try {
            return cacheLevels.size();
        } finally {
            lock.readLock().unlock();
        }
    }

    public CacheLevel<K, V> getCacheLevel(int index) {
        lock.readLock().lock();
        try {
            if (index < 0 || index >= cacheLevels.size()) {
                throw new IndexOutOfBoundsException("Invalid cache level index");
            }
            return cacheLevels.get(index);
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public String toString() {
        lock.readLock().lock();
        try {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < cacheLevels.size(); i++) {
                sb.append("Level ").append(i).append(": ").append(cacheLevels.get(i).getAll()).append("\n");
            }
            return sb.toString();
        } finally {
            lock.readLock().unlock();
        }
    }
}