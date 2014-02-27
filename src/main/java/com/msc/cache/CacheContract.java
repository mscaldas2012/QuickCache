package com.msc.cache;

import com.msc.cache.initializer.CacheInitializerPolicy;
import com.msc.cache.cleanup.CacheCleanupPolicy;
import com.msc.cache.notifier.CacheNotifierPolicy;
import com.msc.cache.loader.CacheLoaderPolicy;

import java.util.Collection;
import java.util.List;

/**
 *
 * @author <a href="mailto:mscaldas@gmail.com">Marcelo Caldas</a>
 */
public interface CacheContract {
	/**
	 * Every time an entity gets invalidated somewhere(deleted from persistence store, or not accessible any
	 * more for any reasons, etc) the responsible class can call this method to make sure that the instance
	 * get's invalidated at the cache level as well.
	 * Again clients should consider the Cache local and be aware of invalidating only at their local Cache
	 * Manager. If the cache is running remotely on other JVMs, is the cache manager responsibility to
	 * synchronize the cache.
	 *
	 * @param entity The entity that is being invalidated.
	 */
	public void invalidate(Cacheable entity) throws Exception;
	/**
	 * Every time an entity gets created or modified somewhere, the responsible class can call this method
	 * to make sure that the instance get's refreshed at the cache level as well.
	 * Again clients should consider the Cache local and be aware of refreshing only at their local Cache
	 * Manager. If the cache is running remotely on other JVMs, is the cache manager responsibility to
	 * synchronize the cache.
	 */
	public void refresh(Cacheable entity) throws Exception;
	/**
	 * Read Method - This method is used when the user wants to extract one instance of a cached entity
	 * out of the cache.
	 *
	 * @param cacheKey The unique identifier of the specific entity the user wants.
	 * @return An instance of the cached entity or null if none found.
	 */
	public Cacheable get(Object cacheKey) throws Exception;
	/**
	 * This method is very similar to the get(cacheKey) method, with the difference that if it's a
	 * miss, it will return null instead of trying to use the loader policy to find the entity.
	 * @param cacheKey
	 * @return
	 * @throws Exception
	 */
	public Cacheable peek(Object cacheKey) throws Exception;
	/**
	 * Read Method - This method is used to retrive from this cache a specific group of cached entities.
	 * PS.: If the groups are deployed as Not Atomics (this.atomicGroup = false)
	 * We are not responsible for the completness of the group. I. e.,
	 * It can happen that the group does not have the complete set of Cacheable entities
	 * of the underlying storage.
	 * If this is not the behavior you want, you need to deploy with atomicGroup = true.
	 *
	 * @param groupKey The identifier of the Group that the user wants to extract from the cache.
	 * @return A Collection with all cached entities that belongs to the specified group.
	 */
	public Collection<Cacheable> getByGroup(Object groupKey) throws Exception;
	/**
	 * This method is used only when the CacheManager is using the Default Group.
	 * By no means we'll expose all groups at once, since this will restrict on how we implement it.
	 * Besides, for some grouped cached data, it can be very costly to retrieve all information out of the
	 * database when a "miss" happens.
	 */
	public Collection<Cacheable> getAll() throws Exception;
	/**
	 * This method flushes an entire Group of cached entities. This method can be called by any user that
	 * knows somehow the group is not needed anymore or it's invalid for some external reasons.
	 * P.S.: When refreshing a cached instance the Cache will be synchronized appropriately. The user does
	 * not need to be calling the flush method for this scenario.
	 * Once the group is flushed and a future request comes, the Cache will consider it a miss and will
	 * retrieve the information back from the persistence storage.
	 *
	 * @param groupKey The identifier of the group that needs to be flushed.
	 */
	public void flushGroup(Object groupKey) throws Exception;
	/**
	 * This method removes all instances from the Cache. Again, if you refresh a specific entity or even
	 * a entire group, it does not mean the user has to call this method. The Cache will be refreshed
	 * appropriately.
	 * But, if for some reason, you know that you have stale or bad information on the cache and want to
	 * force a flush of the data, this method is available.
	 */
	public void flushAll() throws Exception;
//	/**
//	 * Getter method for the name property.
//	 *
//	 * @return The current value assinged to the name property.
//	 */
//	public String getName();
//	/**
//	 * Setter method for the name property.
//	 *
//	 * @param newValue The new value to be assingned to the name value.
//	 */
//	public void setName(String newValue) throws Exception;
	/**
	 * Setter method for the initializer policy. Mostly used by the cacheFactory when instantiating
	 * and configuring the CacheManager. A possible JMX implementation may also use this method to change
	 * initialization policies.
	 *
	 * @param newValue An instance of a CacheInitializerPolicy to be used during initialization of this cache.
	 */
	public void setInitializerPolicy(CacheInitializerPolicy newValue) throws Exception;
    public CacheInitializerPolicy getInitializerPolicy() throws Exception;
	/**
	 * Setter method for the list of clean up policies.
	 *
	 * @param newValue A List containing all the clean up policies to be used by this cache manager.-
	 */
	public void setCleanupPolicies(List<CacheCleanupPolicy> newValue) throws Exception;
    public List<CacheCleanupPolicy> getCleanupPolicies() throws Exception;
	/**
	 * Adds a new instance of a CacheCleanupPolicy concrete implementation to the existent list of
	 * clean up policies assossiated with this cache manager.
	 *
	 * @param newValue The new instance of a CacheCleanupPolicy to be included as a cleanup mechanism.
	 */
	public void addCleanupPolicy(CacheCleanupPolicy newValue) throws Exception;
	/**
	 * Setter method for the Notifier policy to be used with this cache.
	 *
	 * @param newValue An instance of a CacheNotifierPolicy to be used for this cache manager.
	 */
	public void setNotifierPolicy(CacheNotifierPolicy newValue) throws Exception;
    public CacheNotifierPolicy getNotifierPolicy() throws Exception;
	/**
	 * Setter method for the loader policy of this cache.
	 *
	 * @param newValue An instance of a CacheLoaderPolicy  to be used with this cache.
	 */
	public void setLoaderPolicy(CacheLoaderPolicy newValue) throws Exception;
    public CacheLoaderPolicy getLoaderPolicy() throws Exception;
	/**
	 * This method uses the list of CacheCleanup policies to delegate the cleanup mechanism of it's
	 * associated entity caches.
	 */
	public void cleanup() throws Exception;
	/**
	 * Getter method for the grouped property.
	 */
	public boolean isGrouped() throws Exception;
	/**
	 * Setter method for the grouped property.
	 *
	 * @param newValue The new value to be assigned to this property.
	 */
	public void setGrouped(boolean newValue) throws Exception;
	/**
	 * Getter method for the highWaterMark property.
	 */
	public long getHighWaterMark() throws Exception;
	/**
	 * Setter method for the highWaterMark property.
	 *
	 * @param highWaterMark The new value to be assigned to this property.
	 */
	public void setHighWaterMark(long highWaterMark) throws Exception;
	/**
	 * Getter method for the treshHold property.
	 */
	public long getTreshHold() throws Exception;
	/**
	 * Setter method for the treshHold property.
	 *
	 * @param treshHold The new value to be assigned to this property.
	 */
	public void setTreshHold(long treshHold) throws Exception;
	/**
	 * Getter method for the lowWaterMark property.
	 */
	public long getLowWaterMark() throws Exception;
	/**
	 * Setter method for the lowWaterMark property.
	 *
	 * @param lowWaterMark The new value to be assigned to this property.
	 */
	public void setLowWaterMark(long lowWaterMark) throws Exception;
	/**
	 * Getter method for the distributable property.
	 */
	public boolean isDistributable() throws Exception;
	/**
	 * Setter method for the distributable property.
	 *
	 * @param distributable The new value to be assigned to this property.
	 */
	public void setDistributable(boolean distributable) throws Exception;
	/**
	 * Getter method for the syncCache property.
	 */
	public boolean isSyncCluster() throws Exception;
	/**
	 * Setter method for the syncCache property.
	 *
	 * @param syncCache The new value to be assigned to this property.
	 */
	public void setSyncCluster(boolean syncCache) throws Exception;
	/**
	 * Getter method for the atomicGroup property.
	 */
	public boolean isAtomicGroup() throws Exception;
	/**
	 * Setter method for the atomicGroup property.
	 *
	 * @param atomicGroup The new value to be assigned to this property.
	 */
	public void setAtomicGroup(boolean atomicGroup) throws Exception;
	/**
	 * Getter method for the defaultIdleTime property.
	 */
	public long getDefaultIdleTime() throws Exception;
	/**
	 * Setter method for the defaultIdleTime property.
	 *
	 * @param defaultIdleTime The new value to be assigned to this property.
	 */
	public void setDefaultIdleTime(long defaultIdleTime) throws Exception;
	/**
	 * Getter method for the defaultTimeToLive property.
	 */
	public long getDefaultTimeToLive() throws Exception;
	/**
	 * Setter method for the defaultTimeToLive property.
	 *
	 * @param defaultTimeToLive The new value to be assigned to this property.
	 */
	public void setDefaultTimeToLive(long defaultTimeToLive) throws Exception;
	/**
	 * Getter method for the missCounter property.
	 */
	public long getMissCounter() throws Exception;
	/**
	 * Getter method for the hitCounter property.
	 */
	public long getHitCounter() throws Exception;
	/**
	 * Calculates the ratio of hits and misses (evaluate how well the cache is performing...)
	 * a value closer to 0 means that the cache is going very frequent to the persistence storage for the
	 * information. while a value closer to 1 means that the cache almost always have the information
	 * available ready for delivery.
	 *
	 * @return The ration of hits and misses.
	 */
	public double getHitRatio() throws Exception;
    /**
     * Retrieves the current size of the cache.
     *
     * @return the current size of the cache.
     * @throws Exception
     */
    public long getSize() throws Exception;

}
