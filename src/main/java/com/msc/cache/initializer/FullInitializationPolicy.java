package com.msc.cache.initializer;

import com.msc.cache.loader.CacheLoaderPolicy;

import java.util.Collection;

/**
 * <P>Concrete implementation of a CacheInitializerPolicy contract.</P>
 * <P>This initialization policy uses the appropriate loader policy (set up at run time)
 * to load all information necessary for this cache.</P>
 *
 *
 *
 * @author <a href="mailto:mscaldas@gmail.com">Marcelo Caldas</a>
 */
public class FullInitializationPolicy implements CacheInitializerPolicy {
	public Collection init(CacheLoaderPolicy loader) throws Exception {
		return loader.fetchAll();
	}
}
