package com.msc.cache.cleanup;

import com.msc.cache.CacheManager;
/**
 * Contract necessary for the possible implementations of CleanUp mechanisms.
 * Some implementations can be automatically set up, based on the expiration properties
 * of an entity (idle time and timeToLive):
 *
 * <UL>
 * <LI>If you set the defaultIdleTime AND defaultTimeToLive properties, it will use the ExpiredCleanupPolicy</LI>
 * <LI>If you set only defaultIdleTime it will use the IdleTimeCleanupPolicy</LI>
 * <LI>If you set only defaultTimeToLive it will use the TimeToLiveCleanupPolicy.</LI>
 * </UL>
 *
 * Beyond that, you can provide your own clean up policies. Possible Implementations will be LRU (Least Recentrly Used),
 * MRU (Most Recently Used), LFU (Least Frequently used), FBR (Frequency based replacement), FIFO
 * (First In, First Out), LIFO (Last In, First Out), etc.
 * TODO:: Verify whether we need a notification mechanism for the cleanup policies based on changes occurred
 * TODO:: at the cache.
 *
 * @author <a href="mailto:mscaldas@gmail.com">Marcelo Caldas</a>
 */
public interface CacheCleanupPolicy {
	public void cleanup(CacheManager cacheManager);
    public void setFrequency(int frequency);
    public int getFrequency();
}
