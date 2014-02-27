package com.msc.cache.notifier;

import com.msc.cache.Cacheable;

/**
 *
 * @author <a href="mailto:mscaldas@gmail.com">Marcelo Caldas</a>
 */
public class NotificationMessage {
    public static final String INVALIDATION_MESSAGE = "invalidate";
    public static final String REGISTER_MESSAGE = "register";
    public static final String REFRESH_MESSAGE = "refresh";
    public static final String CACHE_HIT_INSTANCE = "cacheHitInstance";
    public static final String CACHE_MISS_INSTANCE = "cacheMissInstance";

    public static final String CACHE_HIT_GROUP = "cacheHitGroup";
    public static final String CACHE_MISS_GROUP = "cacheMissGroup";

    public static final String CACHE_HIT_ALL = "cacheHitAll";
    public static final String CACHE_MISS_ALL = "cacheMissAll";


    private String message;
    private Cacheable entity;
    private Object key;
    private Object groupKey;


    public NotificationMessage(String message) {
        this.message = message;
    }

    public NotificationMessage(String message, Cacheable entity) {
        this.message = message;
        this.entity = entity;
    }

    public NotificationMessage(String message, Object key) {
        this.message = message;
        this.key = key;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Cacheable getEntity() {
        return entity;
    }

    public void setEntity(Cacheable entity) {
        this.entity = entity;
    }

    public Object getKey() {
        return key;
    }

    public void setKey(Object key) {
        this.key = key;
    }

    public Object getGroupKey() {
        return groupKey;
    }

    public void setGroupKey(Object groupKey) {
        this.groupKey = groupKey;
    }
}
