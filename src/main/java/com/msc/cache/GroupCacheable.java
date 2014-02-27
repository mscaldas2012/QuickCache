package com.msc.cache;

/**
 * If a java object has to be cached in a grouped manner, it will have to implement this interface.
 * In this scenario, the cacheKey property still serves as a unique identifier. The groupKey is used
 * only for bulk retrieval of information.
 *
 * @author <a href="mailto:mscaldas@gmail.com">Marcelo Caldas</a>
 */
public interface GroupCacheable extends Cacheable {
	public Object getGroupKey();
    public long  size();
}
