package com.msc.cache.loader;

import com.msc.cache.CacheException;
import com.msc.cache.Cacheable;

import java.util.Collection;

/**
 * This Policy is responsible for handling how to load information from the underlying persistence
 * storage mechanism.
 * For each Cacheable entity you have, or for each CacheManager you will configure for your system,
 * you must create a Class implementing this Policy to "teach" the CacheManager how to load
 * appropriately information out of the underlying storage.
 *
 * Since this is a simple interface, you might be able to use existing framework to retrieve
 * data for the specific cache we're deploying. In this case all you have to do is make
 * this class to implement this interface also, and declare it on the cache-config.xml file.
 * Implement the methods below, making sure that the data retrieved is ready to be cached.
 *
 * @author <a href="mailto:mscaldas@gmail.com">Marcelo Caldas</a>
 */
public interface CacheLoaderPolicy {
	/**
	 * This method shoul be able to retrieve a single instance of a Cacheable entity from the persistence
	 * storage being used for this entity.
	 *
	 * @param cacheKey The identifier of the cacheable entity we're loading.
	 * @return A Single Cacheable instance. Null if not found.
	 */
	public Cacheable fetchEntity(Object cacheKey) throws CacheException;
	/**
	 * This method should be capable of retrieving all possible cacheable entities from the underlying
	 * persistence storage.
	 *
	 * @return A Collection with all Cacheable entities existent. An empty collection if none found.
	 */
	public Collection<Cacheable> fetchAll() throws CacheException;
}
