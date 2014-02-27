package com.msc.cache.initializer;

import com.msc.cache.Cacheable;
import com.msc.cache.loader.CacheLoaderPolicy;

import java.util.Collection;

/**
 * Contract to control how initialization policies may work for different caches.
 * A generic implementation for this policy is the FullInitializationPolicy.
 *
 * It is optional for a cache to have a CacheInitializerPolicy. If the cache does not
 * have one, the cacheManager is treated as a lazy initialization where no data is retrieved from
 * the DB.
 *
 * @author <a href="mailto:mscaldas@gmail.com">Marcelo Caldas</a>
 */
public interface CacheInitializerPolicy {
	public Collection<Cacheable> init(CacheLoaderPolicy loader) throws Exception;
}
