package com.msc.cache;

/**
 * This interface marks any Java Object as Cacheable. It will require two methods to be implemented:
 * setCacheKey(Object) and getCacheKey(). Those methods will make sure that we can identify an object
 * inside the cache. Any time an user wants to retrieve a specific instance of an object from the cache,
 * he will use the cacheKey property.
 * The cacheKey property is defined as object for greater flexibility of how to define the property.
 * Also the equals method is very important on any Cacheable object to be able to identify wheter a specific
 * object instance is already part of the cache or not.
 *
 * @author <a href="mailto:mscaldas@gmail.com">Marcelo Caldas</a>
 */
public interface Cacheable {
	public Object getCacheKey();
}
