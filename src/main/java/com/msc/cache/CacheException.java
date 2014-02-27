package com.msc.cache;


/**
 * <P></P>
 *
 *
 *
 * @author <a href="mailto:mscaldas@gmail.com">Marcelo Caldas</a>
 *
 * @errorMessage error.platform.cache.configurationProblem=There's a problem with the configuration on cache-config.xml. Cause: {0}
 * @errorMessage error.platform.cache.loaderProblem=A Problem occurred while loading data for cache.
 * @errorMessage error.platform.cache.invalidManager=Invalid managerName. Could not find manager for {1}
 * @errorMessage error.platform.cache.initializationError=Could not load Factory. Please check your cache-config.xml
 * @errorMessage error.platform.cache.lockingProblem=Problems locking the cache for read.
 * @errorMessage error.platform.cache.notAGroup=Invalid method call. Loader is not a Group Loader.
 * @errorMessage error.platform.cache.invalidGroup=Invalid GroupCacheable: unable to mix groups!
 */
public class CacheException extends Exception {
	private static final String ERROR_KEY="error.platform.cache.configurationProblem";
	public static final String ERROR_NOT_A_GROUP="error.platform.cache.notAGroup";
	public static final String ERROR_INVALID_GROUP="error.platform.cache.invalidGroup";
	public CacheException(String msg, String problem) {
		super(msg +  problem);
	}
}
