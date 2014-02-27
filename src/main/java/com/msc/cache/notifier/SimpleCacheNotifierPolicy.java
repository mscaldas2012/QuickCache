package com.msc.cache.notifier;


/**
 * @author <a href="mailto:mscaldas@gmail.com">Marcelo Caldas</a>
 */
public class SimpleCacheNotifierPolicy implements CacheNotifierPolicy {
    public void notifyCache(NotificationMessage message) {
        if (message.equals(NotificationMessage.INVALIDATION_MESSAGE)) {
            this.notifyInvalidateMessage(message);
        } else if (message.equals(NotificationMessage.REGISTER_MESSAGE)) {
            this.notifyRegisterMessage(message);
        } else if (message.equals(NotificationMessage.REFRESH_MESSAGE)) {
            this.notifyRefreshMessage(message);
        } else if (message.equals(NotificationMessage.CACHE_HIT_INSTANCE)) {
            this.notifyHitInstanceMessage(message);
        } else if (message.equals(NotificationMessage.CACHE_MISS_INSTANCE)) {
            this.notifyMissInstanceMessage(message);
        } else if (message.equals(NotificationMessage.CACHE_HIT_GROUP)) {
            this.notifyHitGroupMessage(message);
        } else if (message.equals(NotificationMessage.CACHE_MISS_GROUP)) {
            this.notifyMissGroupMessage(message);
        } else if (message.equals(NotificationMessage.CACHE_HIT_ALL)) {
            this.notifyHitAllMessage(message);
        } else if (message.equals(NotificationMessage.CACHE_MISS_ALL)) {
            this.notifyMissAllMessage(message);
        }

    }


    protected void notifyInvalidateMessage(NotificationMessage message) {
    }

    protected void notifyRegisterMessage(NotificationMessage message) {
    }

    protected void notifyRefreshMessage(NotificationMessage message) {
    }

    protected void notifyHitInstanceMessage(NotificationMessage message) {
    }

    protected void notifyMissInstanceMessage(NotificationMessage message) {
    }
    protected void notifyHitGroupMessage(NotificationMessage message) {
    }
    protected void notifyMissGroupMessage(NotificationMessage message) {
    }
    protected void notifyHitAllMessage(NotificationMessage message) {
    }
    protected void notifyMissAllMessage(NotificationMessage message) {
    }
}
