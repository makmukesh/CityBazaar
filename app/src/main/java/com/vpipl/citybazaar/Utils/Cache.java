package com.vpipl.citybazaar.Utils;

import androidx.collection.LruCache;

/**
 * Created by Mukesh on 27/12/2019.
 */

public class Cache {
    private static Cache instance;
    private LruCache<Object, Object> lru;
    private int cacheSize = 100 * 1024 * 1024;

    private Cache() {
        lru = new LruCache<>(cacheSize);
    }

    public static Cache getInstance() {
        if (instance == null) {
            instance = new Cache();
        }
        return instance;
    }

    public LruCache<Object, Object> getLru() {
        return lru;
    }
}