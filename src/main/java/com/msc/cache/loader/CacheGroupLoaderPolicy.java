package com.msc.cache.loader;


import com.msc.cache.CacheException;
import com.msc.cache.Cacheable;
import com.msc.cache.GroupCacheable;

import java.util.Collection;

/**
 * An extension of the CacheLoaderPolicy. If your Cacheable instance is actually a GroupCacheable -
 * it can be grouped within the cache, the Loader you create must extend this Policy, so we also
 * have the capability of retrieving all the Cacheable entities based on a groupKey.
 *
 * Since this is a simple interface, you might be able to use existing framework to retrieve
 * data for the specific cache we're deploying. In this case all you have to do is make
 * this class to implement this interface also, and declare it on the cache-config.xml file.
 * Implement the methods below, making sure that the data retrieved is ready to be cached.
 *
 * @author <a href="mailto:mscaldas@gmail.com">Marcelo Caldas</a>
 */
public interface CacheGroupLoaderPolicy extends CacheLoaderPolicy {
	/**
	 * This method retrieves all existent groups in the underlying persistence storage.
	 *
	 * Very useful when you want to initialize a cache by loading only the possible groups.
	 * @return A Collection of Groups - not real entities,
	 * @throws Exception
	 */
	public Collection<GroupCacheable> fetchGroups() throws CacheException;
	/**
	 * This method should be capable of retrieving all the Cacheable entities on the underlying persistence
	 * storage that belongs to the same groupkey, passed as a parameter for this method.
	 *
	 * @param groupKey The identifier of which group we're loading out of the persistence storage.
	 * @return A Collection with all Cacheable entities that belongs to the same groupKey. An Empty collection if none was found
	 */
	public Collection<Cacheable> fetchByGroup(Object groupKey) throws CacheException;
}
