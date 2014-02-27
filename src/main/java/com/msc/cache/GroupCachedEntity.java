package com.msc.cache;

import java.util.*;

/**
 * @author <a href="mailto:mscaldas@gmail.com">Marcelo Caldas</a>
 */
public class GroupCachedEntity extends CachedEntity implements GroupCacheable {
	private Map<Object, CachedEntity> cachedEntities = new HashMap<Object, CachedEntity>();
	//In case the Cacheable has other keys it can be referenced by, we'll populate
	//this Array of Map - one map for each key, with those secondary keys.
	private Map<Object, CachedEntity>[] secondaryReferences;

	private boolean atomicGroup;
	/**
	 * this property keeps track of the Group Key this Cached entity is handling.
	 */
	private Object groupKey;

	public GroupCachedEntity() {
		super();
	}

	public Object getGroupKey() {
		return this.groupKey;
	}

//	private Map getCachedEntities() {
//		return cachedEntities;
//	}
//
	public Iterator iterator() {
		return this.cachedEntities.values().iterator();
	}
	public CachedEntity get(Object key) {
		Object result = this.cachedEntities.get(key);
		if (result == null && this.secondaryReferences != null) {
            for (Map secondaryReference : secondaryReferences) {
                result = secondaryReference.get(key);
            }
		}
		return (CachedEntity) result;
	}

	public void removeEntity(Object cacheKey) {
		this.cachedEntities.remove(cacheKey);
	}

	public void addCachedEntity(CachedEntity newValue) throws CacheException {
		Object newGroupKey = CacheManager.DEFAULT_GROUP_KEY;
		final Cacheable entity = newValue.getEntity();
		if (entity instanceof GroupCacheable) {
			newGroupKey = ((GroupCacheable) entity).getGroupKey();
		}
		if (this.groupKey != null && !newGroupKey.equals(this.groupKey)) {
			throw new CacheException("Invalid GroupCacheable: unable to mix groups!", CacheException.ERROR_INVALID_GROUP);
		}
		this.cachedEntities.put(newValue.getCacheKey(), newValue);
		//Populate the secondaryReferences Maps
		if (entity instanceof CompoundKeyCacheable) {
			Object[] keys = ((CompoundKeyCacheable) entity).getSecondaryKeys();
			if (this.secondaryReferences == null) {
				this.secondaryReferences = new HashMap[keys.length];
			}
			for (int i = 0; i < keys.length; i++) {
				this.secondaryReferences[i].put(keys[i], newValue);
			}
		}

		if (this.groupKey == null) {
			this.groupKey = newGroupKey;
		}
	}

	public void setAtomicGroup(boolean newValue) {
		this.atomicGroup = newValue;
	}

	public boolean isAtomicGroup() {
		return this.atomicGroup;
	}

	public long size() {
		return this.cachedEntities.size();
	}

	/**
	 * This method will remove the entity marked by this key from the cache!
	 *
	 * @param cacheKey The key for the cached object we want to remove.
	 */
	public void remove(Object cacheKey) {
		CachedEntity entity = this.cachedEntities.get(cacheKey);
		// Since cacheKey could be any of the available keys for a specific entity,
		// We need to make sure we get the Primary key first before trying to remove it!

		if (this.secondaryReferences != null) {
			for (int i = 0; i < secondaryReferences.length && entity != null; i++) {
				entity = secondaryReferences[i].get(cacheKey);
				if (entity != null) {
					cacheKey = entity.getCacheKey();
				}
			}
			//Make sure we clean all Maps before deleting the stuff
			if (entity instanceof CompoundKeyCacheable) {
				Object[] keys = ((CompoundKeyCacheable) entity).getSecondaryKeys();
				for (int i = 0; i < keys.length; i++) {
					this.secondaryReferences[i].remove(keys[i]);
				}
			}
		}
		this.cachedEntities.remove(cacheKey);
	}

	/**
	 * helper method to extract the "real" entities out of a collection of CachedEntities, to give it back to
	 * the user.
	 *
	 * @return A collection of "real" entities ready to be given for user manipulation.
	 */
	public Collection<Cacheable> extractEntities() {
		Collection<Cacheable> result = new ArrayList<Cacheable>();
        for (CachedEntity entity : this.cachedEntities.values()) {
            entity.hit();
            result.add(entity.getEntity());
        }
		return result;
	}
}
