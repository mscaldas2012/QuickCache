package com.msc.cache;

/**
 * This class acts as a wrapper for any Cacheable java object being placed on a Cache.
 * It is the class responsible for controling the statistics of each entity, for proper analisys
 * of clean up, notification and loading mechanisms of the CacheManager.
 *
 * @author <a href="mailto:mscaldas@gmail.com">Marcelo Caldas</a>
 */
public class CachedEntity implements Cacheable {
	/**
	 * Defines the idle Time as undefined, meaning that an object can
	 * theoretically be idle for an unlimited time.
	 */
	public static final long UNDEFINED_IDLE_TIME = -1;
	/**
	 * Defines the time to live as undefined, meaning that an object can
	 * theoretically be live for an unlimited time.
	 */
	public static final long UNDEFINED_TIME_TO_LIVE = -1;

	/**
	 * The maxIdleTime specifies how long an object can live on the cache without any hit.
	 * if the maxIdleTime is UNDEFINED, then it is not taken into consideration during clean up
	 * mechanism.
	 * Idle Time is defined in Number of Seconds.
	 */
	private long maxIdleTime;
	/**
	 * Every time a object receives a hit, the lastAccessedTime is updated, for future recalculation of
	 * cleanup when maxIdleTime is defined.
	 */
	private long lastAccessedTime ;

	/**
	 * The maxTimeToLive specifies how long an object can live on the cache.
	 * if the maxTimeToLive is UNDEFINED, then it is not taken into consideration during clean up
	 * mechanism.
	 */
	private long maxTimeToLive;
	/**
	 * Every time a new entity is registered, the creationTime is set for the current timestamp of the system.
	 * This property is used for future cleanup mechanisms when maxTimeToLive is defined.
	 */
	private long creationTime ;

	/**
	 * This property points to the actual entity being cached. This is the information that a user wants
	 * when hitting a cache.
	 */
	private Cacheable entity;

	/** a boolean indicating whether the entity has been invalidated or not. */
	private boolean invaldiated;

	/**
	 * Counter for how many times this entity have been hit.
	 * This property is used for cleanup mechanisms based on the number of hits (least resource used, etc)
	 */
	private long numberOfHits;

    public CachedEntity() {
        long currentTime = System.currentTimeMillis();
        this.lastAccessedTime = currentTime;
        this.creationTime = currentTime;
    }

	public Object getCacheKey() {
		return this.entity.getCacheKey();
	}

	public long getMaxIdleTime() {
		return maxIdleTime;
	}

	public void setMaxIdleTime(long maxIdleTime) {
		this.maxIdleTime = maxIdleTime;
	}

	public long getLastAccessedTime() {
		return lastAccessedTime;
	}
	public long getMaxTimeToLive() {
		return maxTimeToLive;
	}

	public void setMaxTimeToLive(long maxTimeToLive) {
		this.maxTimeToLive = maxTimeToLive;
	}

	public long getCreationTime() {
		return creationTime;
	}
	public Cacheable getEntity() {
		return entity;
	}

	public void setEntity(Cacheable entity) {
		this.entity = entity;
	}

	public boolean isInvaldiated() {
		return invaldiated;
	}

	public void setInvaldiated(boolean invaldiated) {
		this.invaldiated = invaldiated;
	}

	public long getNumberOfHits() {
		return numberOfHits;
	}
	public void hit() {
		this.numberOfHits++;
		this.lastAccessedTime = System.currentTimeMillis();
	}
}
