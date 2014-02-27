package com.msc.cache;


import com.msc.cache.cleanup.CacheCleanupPolicy;
import com.msc.cache.cleanup.ExpiredCleanupPolicy;
import com.msc.cache.cleanup.IdleTimeCleanupPolicy;
import com.msc.cache.cleanup.TimeToLiveCleanupPolicy;
import com.msc.cache.initializer.CacheInitializerPolicy;
import com.msc.cache.loader.CacheGroupLoaderPolicy;
import com.msc.cache.loader.CacheLoaderPolicy;
import com.msc.cache.notifier.CacheNotifierPolicy;
import com.msc.cache.notifier.NotificationMessage;

import java.util.*;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.logging.Logger;

/**
 * <p>The CacheManager is responsible for holding entities being cached. It is also responsible
 * for holding all the behavior policies for this cache.</P>
 * <P>The CacheManager per say, is implemented as if it runs locally on a single machine. Meaning, it
 * has the capability of refreshing itself and register and invalidate cached entities. In the case
 * that the CacheManager is deployed distributed throughout a clustered environment, this Class relies
 * on having a concrete implementation of a NotificationPolicy which will take care of the notification
 * and synchronization of the remote caches.<P>
 * <P>The user perspective:<BR>
 * =====================<BR>
 * Most of the times, users or cache clients will be using the "read" methods of a cacheManager.
 * get(CacheKey) getByGroup(groupKey) and getAll() methods are the "read" methods - meaning, they provide
 * information back to the client.
 * </P>
 * <P>The developer perspective:<BR>
 * ==========================<BR>
 * The developer must take into consideration the "read" methods described above and also the methods
 * that identify changes to the underlying persistence storage to the cache.
 * Meaning, Classes (or layers) responsible for Create, Update or  Delete should inform their
 * respective caches after committing this information to the DB to refresh their information.
 * <p/>
 * Sometimes this refresh can be synchronized with the DB persistence. some times it can be delayed.
 * but that is up to the other layers. All the cache cares is that he will receive an invalidate
 * or refresh message informing him what to do.
 * </P>
 *
 * @author <a href="mailto:mscaldas@gmail.com">Marcelo Caldas</a>
 */
public class CacheManager implements CacheContract {
	/**
	 * Every CacheManager has Groups. If the entities being cached has to be grouped somehow, meaning, they
	 * implement the GroupCacheable interface, the CacheManager will make sure each entity ends up on the right
	 * cache.
	 * But for entities that implements only the Cacheable interface (not grouped), they will reside on the
	 * Default Group created and managed by the cache manager.
	 */
	public static final Long DEFAULT_GROUP_KEY = -1L;
	/**
	 * Unique identifier for this CacheManager. Used on the configuration file to identify each cache and also
	 * for the CacheFactory loader to keep track of all the caches being deployed.
	 */
	private String name;
	/**
	 * Those will be implemented in the near future... They are not that necessary for a v1.0
	 * of our caching mechanism. It's more like a enhancement!
	 */
	private long highWaterMark;
	/**
	 * Those will be implemented in the near future... They are not that necessary for a v1.0
	 * of our caching mechanism. It's more like a enhancement!
	 */
	private long treshHold;
	/**
	 * Those will be implemented in the near future... They are not that necessary for a v1.0
	 * of our caching mechanism. It's more like a enhancement!
	 */
	private long lowWaterMark;

	/**
	 * Default idle time applied to each entity and group being added to the cache.
	 * The idle time is the maximum number of seconds an entity can exist without a "hit" before being
	 * expired and discarded by the clean up mechanism.
	 */
	private long defaultIdleTime = -1;
	/**
	 * Default time to live applied to each entity and group being added to the cache.
	 * The timeToLive is the maximum number of seconds an entity can exist after creation. Then it will be
	 * expired and discarded by the clean up mechanism.
	 */
	private long defaultTimeToLive = -1;

	/**
	 * Whether this Cache is running distributed on a cluster or run only locally on this JVM.
	 */
	private boolean distributable = false;
	/**
	 * Whether, when the cache is running across a cluster, if we have to maintain the caches synchronized
	 * or not.
	 */
	private boolean syncCluster = false;
	/**
	 * This property holds all the possible groups available on the cache.
	 * Each group will hold the entities cached under that group.
	 * This hashMap holds as key the groupKey object and as value a Instance of GroupCachedEntity
	 * which holds the attributes for a group globally and a Collection of cachedEntities.
	 * If the CacheManager holds entities that are not grouped, internally it will have a
	 * Default Group which will hold all the entities that are not grouped. The user is not aware of this
	 * default group. It is only a programming trick to make the CacheManager capable of handling
	 * both types of Cacheable entities.
	 * Groups are completely handled internally. Everytime a Cacheable entity is being registered,
	 * this Manager will check whether it is a GroupCacheable or a simple Cacheable.
	 * If it is a simple Cacheable, it will be placed on the "Default" group.
	 * Otherwise, it will figure out the group that it belongs and place it (creating a Group if necessary)
	 * on the right group!
	 */
	private Map<Object, GroupCacheable> groups = new HashMap<Object, GroupCacheable>();
	/**
	 * Identifies whether the entities on this group are treated as atomic - one single unit of information.
	 * Meaning: if a piece of information changes, the entire group is affected.
	 * If this variable is set to true, everytime a Cacheable has to be refreshed, its entire group has to
	 * be refreshed. Otherwise, only the specific instance is refreshed.
	 */
	private boolean atomicGroup = false;
	/**
	 * Simple counter for all miss that this cache has suffered.
	 */
	private long missCounter;
	/**
	 * Simple counter for all the hits this cache has achieved. The hitCounter for the cache is more "persistent"
	 * than the individuals hit of each entity, since after an entity is cleaned-up we loose the track
	 * of how many hits it had in the past.
	 */
	private long hitCounter;


	/**
	 * Policies:
	 * The initializer policy controls how the Cache is started. If this variable is left NULL, it means
	 * that there's no policy for initialization - or better yet - The cache uses a Lazy initialization,
	 * where he does not load anything before hand.
	 * Some Generic implementations exists like FullInitializationPolicy. And the initializations can be as
	 * specific as needed for you scenario. The only requirement is they have to implement the
	 * InitializerPolicy interface.
	 */
	private CacheInitializerPolicy initializerPolicy;
	/**
	 * Holds the list of all cleaup mechanisms necessary for this Cache. It is a list because a cache might have
	 * more than one cleanup mechanism. Actually, by providing a defaultIdleTime and/or defaultTimeToLive
	 * it automatically enables the appropriate cleanup mechanism for Expired entities:
	 * <UL>
	 * <LI>If you set the defaultIdleTime AND defaultTimeToLive properties, it will use the ExpiredCleanupPolicy</LI>
	 * <LI>If you set only defaultIdleTime it will use the IdleTimeCleanupPolicy</LI>
	 * <LI>If you set only defaultTimeToLive it will use the TimeToLiveCleanupPolicy.</LI>
	 * </UL>
	 * Beyond that, you can provide your own clean up policies. Possible Implementations will be LRU (Least Recently Used),
	 * MRU (Most Recently Used), LFU (Least Frequently used), FBR (Frequency based replacement), FIFO
	 * (First In, First Out), LIFO (Last In, First Out), etc.
	 * TODO:: Verify whether we need a notification mechanism for the cleanup policies based on changes occurred
	 * at the cache.
	 */
	private List<CacheCleanupPolicy> cleanupPolicies = new ArrayList<CacheCleanupPolicy>();
	/**
	 * When the cache is running distributed, we have to somehow notify Caches on different JVMs. For this
	 * purpose, we can plug in a notification mechanism.
	 * If there's no notifier policy, it will be treated as a local cache with no need for synchronization.
	 */
	private CacheNotifierPolicy notifierPolicy;
	/**
	 * This Policy doesn't provide any generic behavior. Basically because each cached entity is very specific
	 * and we have to somehow teach the Cache how to retrieve it's information.
	 * Again, CacheManagers are ReadOnly - meaning, they hold persisted data, they never care about persisting
	 * their data - only refreshing it from a storage.
	 */
	private CacheLoaderPolicy loaderPolicy;
	/**
	 * Whether the information being cached is grouped (the Entities extend GroupCacheable) or not (the
	 * entitites extend Cacheable interface).
	 * This information is important for when the cache is empty and it does not have any mechanisms to check
	 * whether it is grouped or not: No groups exists yet and no entities are cached.
	 */
	private boolean grouped;


	private Logger logger = Logger.getLogger(CacheManager.class.getName());
    private ArrayList<CacheCleanupThread> childThreads = new ArrayList<CacheCleanupThread>();
    private ReadWriteLock lock = new ReentrantReadWriteLock();


    /**
	 * Current cacheKey does not exist on the cache yet...
	 * Create a new CachedEntity and add it to the cache.
	 *
	 * @param entity The new entity being registered into the cache!
	 */
	protected void register(Cacheable entity) throws CacheException {
		logger.fine("Registering new entity: " + entity.getCacheKey());
		CachedEntity newEntity = new CachedEntity();
		newEntity.setMaxIdleTime(this.getDefaultIdleTime());
		newEntity.setMaxTimeToLive(this.getDefaultTimeToLive());
		newEntity.setEntity(entity);
		//add the new entity into the cache:
		if (entity instanceof GroupCacheable) {
			//Find his group:
			Object groupKey = ((GroupCacheable) entity).getGroupKey();
			logger.finest("\tUnder Group: " + groupKey);
			GroupCachedEntity aGroup = getGroupUnconditional(groupKey);
			aGroup.addCachedEntity(newEntity);
		} else { //Add the entity to the Default group:
			GroupCachedEntity defaultGroup = getGroupUnconditional(DEFAULT_GROUP_KEY);
			defaultGroup.addCachedEntity(newEntity);
		}
		NotificationMessage nf = new NotificationMessage(NotificationMessage.REGISTER_MESSAGE, entity);
		this.notifyCache(nf);

	}
	/**
	 * This method tries to find the requested group on the current collection of groups.
	 * If it exists, it will return this group.
	 * If it does Not exists, it will create a new Group; add it to the list of groups and return that new
	 * instance
	 *
	 * @param groupKey The group key we're looking for.
	 * @return An instance of GroupCachedEntity.
	 */
	protected GroupCachedEntity getGroupUnconditional(Object groupKey) {
		GroupCachedEntity group = (GroupCachedEntity) this.groups.get(groupKey);
		if (group == null) {
			group = new GroupCachedEntity();
			group.setMaxIdleTime(this.getDefaultIdleTime());
			group.setMaxTimeToLive(this.getDefaultTimeToLive());
			group.setAtomicGroup(this.isAtomicGroup());
			logger.finest("Adding group " + groupKey);
			this.groups.put(groupKey, group);
		}
		return group;
	}

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
	public void invalidate(Cacheable entity) {
		logger.fine("Invalidating entity: " + entity.getCacheKey());
		GroupCachedEntity aGroup;
		if (entity instanceof GroupCacheable) {
			aGroup = (GroupCachedEntity) this.groups.get(((GroupCacheable) entity).getGroupKey());
		} else {
			aGroup = this.findGroupForEntity(entity.getCacheKey());
		}
		if (aGroup != null) {//Group found invalidate it...
			//if atomicGroup is true, invalidate the entire group.
			if (aGroup.isAtomicGroup()) {
				this.flushGroup(aGroup.getGroupKey());
			} else {
				//otherwise invalidate the entity.
				aGroup.remove(entity.getCacheKey());
			}
			NotificationMessage nf = new NotificationMessage(NotificationMessage.INVALIDATION_MESSAGE, entity);
			this.notifyCache(nf);
		} //Group Not available any more, maybe invalidate elsewhere...

	}
	/**
	 * Every time an entity gets created or modified somewhere, the responsible class can call this method
	 * to make sure that the instance get's refreshed at the cache level as well.
	 * Again clients should consider the Cache local and be aware of refreshing only at their local Cache
	 * Manager. If the cache is running remotely on other JVMs, is the cache manager responsibility to
	 * synchronize the cache.
	 */
	public void refresh(Cacheable entity) throws Exception {
		logger.fine("Refreshing entity: " + entity.getCacheKey());
		GroupCachedEntity aGroup;
		if (entity instanceof GroupCacheable) {
			aGroup = (GroupCachedEntity) this.groups.get(((GroupCacheable) entity).getGroupKey());
		} else {
			aGroup = this.findGroupForEntity(entity.getCacheKey());
		}
		if (aGroup != null) {
			//If atomicGroup is true, invalidate the entire group.
			if (aGroup.isAtomicGroup()) {
				//Have to refresh the Group out of the DB:
				Object groupKey = aGroup.getGroupKey();
				this.flushGroup(groupKey);
				this.fetchByGroup(groupKey);
			} else { //otherwise refresh the entity.
				//aGroup.getCachedEntities().put(entity.getCacheKey(), entity);
				this.register(entity);
			}
			NotificationMessage nf = new NotificationMessage(NotificationMessage.REFRESH_MESSAGE, entity);
			this.notifyCache(nf);
		} else { //Registering a new entity...
			this.register(entity);

		}

	}
	/**
	 * Since Groups are a internal representation of data, sometimes we have to find which group a specific
	 * instance belongs to.
	 *
	 * @param cacheKey The entity being cached for which we need to find the Group it belongs to.
	 * @return The group that holds the cached entity represented by the key.
	 */
	protected GroupCachedEntity findGroupForEntity(Object cacheKey) {
		//First implementation... first finding out on which group it exists...
		//Maybe we need a better (faster approach...)
		boolean found = false;
		GroupCachedEntity aGroup = null;
		for (Iterator iterator = this.groups.values().iterator(); iterator.hasNext() && !found;) {
			aGroup = (GroupCachedEntity) iterator.next();
			found = aGroup.get(cacheKey) != null;
		}
		return (found ? aGroup : null);
	}
	/**
	 * Read Method - This method is used when the user wants to extract one instance of a cached entity
	 * out of the cache.
	 *
	 * @param cacheKey The unique identifier of the specific entity the user wants.
	 * @return An instance of the cached entity or null if none found.
	 */
	public Cacheable get(Object cacheKey) throws Exception {
		logger.fine("Reading object from cache: (" + cacheKey + ")");
		logger.finest("cache class: " + cacheKey.getClass().getName());
		//Try to get the entity out of the cached entities...
		GroupCachedEntity aGroup = this.findGroupForEntity(cacheKey); //IF entity not foud, returns NULL!
		if (aGroup != null) { //If available, great.. It's a hit.
			logger.finest("CacheManager::It's a Hit!!!");
			this.hitCounter++;
			CachedEntity cachedEntity = aGroup.get(cacheKey);
			aGroup.hit();
			cachedEntity.hit();
			NotificationMessage nf = new NotificationMessage(NotificationMessage.CACHE_HIT_INSTANCE, cacheKey);
			this.notifyCache(nf);
			return cachedEntity.getEntity();
		} else { //Otherwise it's a miss... Go fetch it from the DB.
			logger.finest("CacheManager::Ooops! It's a Miss");
			this.missCounter++;
			Cacheable result = this.fetchEntity(cacheKey);
			if (result instanceof GroupCacheable && this.isAtomicGroup()) {
				this.fetchByGroup(((GroupCacheable) result).getGroupKey());
			}
			NotificationMessage nf = new NotificationMessage(NotificationMessage.CACHE_MISS_INSTANCE, cacheKey);
			this.notifyCache(nf);
			return result;
		}
	}

	public Cacheable peek(Object cacheKey) throws Exception {
		logger.fine("Peeking object from cache: (" + cacheKey + ")");
		logger.finest("cache class: " + cacheKey.getClass().getName());
		//Try to get the entity out of the cached entities...
		GroupCachedEntity aGroup = this.findGroupForEntity(cacheKey);
		if (aGroup != null) { //If available, great.. It's a hit.
			CachedEntity cachedEntity = aGroup.get(cacheKey);
			return cachedEntity.getEntity();
		} else { //Otherwise it's a miss... peek returns NULL
			return null;
		}
	}

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
	public Collection<Cacheable> getByGroup(Object groupKey) throws Exception {
		logger.fine("Reading Grouped entities for group: " + groupKey);
		GroupCachedEntity aGroup = (GroupCachedEntity) this.groups.get(groupKey);
		if (aGroup != null) { //If available, great... It's a hit
			logger.finest("CacheManager::It's a Hit!!!");
			aGroup.hit();
			//Extract all Cacheable Entities out of the group and retrieve them
			NotificationMessage nf = new NotificationMessage(NotificationMessage.CACHE_HIT_GROUP);
			nf.setGroupKey(groupKey);
			this.notifyCache(nf);
			return aGroup.extractEntities();
		} else { //Otherwise it's a miss. Go fetch it from the DB.
			logger.finest("CacheManager::Ooops! It's a Miss");
			this.missCounter++;
			Collection<Cacheable> result = this.fetchByGroup(groupKey);
			NotificationMessage nf = new NotificationMessage(NotificationMessage.CACHE_MISS_GROUP);
			nf.setGroupKey(groupKey);
			this.notifyCache(nf);
			return result;
		}
	}
	/**
	 * This method is used only when the CacheManager is using the Default Group.
	 * By no means we'll expose all groups at once, since this will restrict on how we implement it.
	 * Besides, for some grouped cached data, it can be very costly to retrieve all information out of the
	 * database when a "miss" happens.
	 */
	public Collection<Cacheable> getAll() throws Exception {
		logger.fine("Reading all entities");
		if (!isGrouped()) {
			GroupCachedEntity aGroup = (GroupCachedEntity) this.groups.get(DEFAULT_GROUP_KEY);
			if (aGroup != null) { //If available, great... It's a hit
				logger.finest("CacheManager::It's a Hit!!!");
				this.hitCounter++;
				aGroup.hit();
				NotificationMessage nf = new NotificationMessage(NotificationMessage.CACHE_HIT_ALL);
				this.notifyCache(nf);
				//Extract all Cacheable Entities out of the group and retrieve them
				return aGroup.extractEntities();
			} else { //Otherwise it's a miss. Go fetch it from the DB. ONLY if is not grouped
				logger.finest("CacheManager::Ooops! It's a Miss");
				this.missCounter++;
				Collection<Cacheable> result = this.fetchAll();
				NotificationMessage nf = new NotificationMessage(NotificationMessage.CACHE_MISS_ALL);
				this.notifyCache(nf);
				return result;
			}
		}
		return null;
	}

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
	public void flushGroup(Object groupKey) {
		logger.info("Flushing group: " + groupKey);
		this.groups.remove(groupKey);
	}
	/**
	 * This method removes all instances from the Cache. Again, if you refresh a specific entity or even
	 * a entire group, it does not mean the user has to call this method. The Cache will be refreshed
	 * appropriately.
	 * But, if for some reason, you know that you have stale or bad information on the cache and want to
	 * force a flush of the data, this method is available.
	 */
	public void flushAll() {
		logger.info("Flushing Cache...");
		this.groups.clear();
	}

	/**
	 * This method will interface with the CacheLoaderPolicy implementation to retrieve all possible
	 * groups for this cache.
	 *
	 * @return The possible list of groups for a specific entity.
	 */
	protected Collection<GroupCacheable> fetchGroups() throws CacheException {
		if (this.loaderPolicy instanceof CacheGroupLoaderPolicy) {
			Collection<GroupCacheable> group = ((CacheGroupLoaderPolicy) this.loaderPolicy).fetchGroups();
			//Cache all groups under the manager:
			if (group != null) {
                for (GroupCacheable aGroup : group) {
                    this.getGroupUnconditional(aGroup.getGroupKey());
                }
			}
			return group;
		} else {
			throw new CacheException("Invalid method call. Loader is not a GroupLoader", CacheException.ERROR_NOT_A_GROUP);
		}
	}
	/**
	 * Every time a miss happens and the cache has to retrieve a group, it will use this method for
	 * that, which will interface with the CacheLoaderPolicy to be able to retrieve the information.
	 *
	 * @param groupKey the identifier of the group we have to fetch.
	 * @return A collection of all entities that belong to a specific group.
	 */
	protected Collection<Cacheable> fetchByGroup(Object groupKey) throws CacheException {
		if (this.loaderPolicy instanceof CacheGroupLoaderPolicy) {
			Collection<Cacheable> group = ((CacheGroupLoaderPolicy) this.loaderPolicy).fetchByGroup(groupKey);
			//Cache all entities under the manager:
			this.cacheEntities(group);
			return group;
		} else {
			throw new CacheException("Invalid method call. Loader is not a GroupLoader", CacheException.ERROR_NOT_A_GROUP);
		}
	}

	/**
	 * This method interfaces with the CacheLoaderPolicy to retrieve a single instance of a specific
	 * entity
	 *
	 * @param cacheKey The identifier of the entity we're trying to fetch from the persistence storage
	 * @return An instance of a cached entity.
	 */
	protected Cacheable fetchEntity(Object cacheKey) throws CacheException {
		Cacheable entity = this.loaderPolicy.fetchEntity(cacheKey);
		//Cache the entity under the manager:
		if (entity != null) {
			this.register(entity);
		}
		return entity;
	}

	/**
	 * This method interfaces with the CacheLoaderPolicy to retrieve all instances that has to be
	 * cached by this cache manager.
	 *
	 * @return A Collection of entities to be cached by this manager.
	 */
	protected Collection<Cacheable> fetchAll() throws Exception {
		Collection<Cacheable> entities = this.loaderPolicy.fetchAll();
		//Cache all entities under the manager;
		this.cacheEntities(entities);
		return entities;
	}

	/**
	 * This method is used by the CacheFactory to initialize this Manager. But, before this method
	 * is called, the CacheFactory must make sure that:
	 * <UL>
	 * <LI>The Cache Manager is properly instantiated and all attributes set</LI>
	 * <LI>The CacheLoaderPolicy class has been properly set up</LI>
	 * <LI>The CacheInitializerPolicy class has been properly set up</LI>
	 * </UL>
	 */
	protected void init() throws Exception {
        if (getDefaultIdleTime() > 0 && getDefaultTimeToLive() > 0) {
            addCleanupPolicy(new ExpiredCleanupPolicy());
        } else if (getDefaultIdleTime() > 0) {
            addCleanupPolicy(new IdleTimeCleanupPolicy());
        } else if (getDefaultTimeToLive() > 0) {
            addCleanupPolicy(new TimeToLiveCleanupPolicy());
        }
        //Make sure we DO have an initializerPolicy!
		if (this.initializerPolicy != null) {
			Collection<Cacheable> entities = this.initializerPolicy.init(this.loaderPolicy);
			//Cache all entities under the manager:
			this.cacheEntities(entities);
		}
        for (CacheCleanupPolicy cp: this.getCleanupPolicies()) {
            CacheCleanupThread aThread = new CacheCleanupThread(cp.getFrequency(), this);
            this.childThreads.add(aThread);
            aThread.start();
        }

    }

	/**
	 * Getter method for the name property.
	 *
	 * @return The current value assinged to the name property.
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * Setter method for the name property.
	 *
	 * @param newValue The new value to be assingned to the name value.
	 */
	public void setName(String newValue) {
		this.name = newValue;
	}

	/**
	 * Setter method for the initializer policy. Mostly used by the cacheFactory when instantiating
	 * and configuring the CacheManager. A possible JMX implementation may also use this method to change
	 * initialization policies.
	 *
	 * @param newValue An instance of a CacheInitializerPolicy to be used during initialization of this cache.
	 */
	public void setInitializerPolicy(CacheInitializerPolicy newValue) {
		this.initializerPolicy = newValue;
	}
	public CacheInitializerPolicy getInitializerPolicy() {
		return this.initializerPolicy;
	}

	/**
	 * Setter method for the list of clean up policies.
	 *
	 * @param newValue A List containing all the clean up policies to be used by this cache manager.-
	 */
	public void setCleanupPolicies(List<CacheCleanupPolicy> newValue) {



        this.cleanupPolicies = newValue;
	}
	public List<CacheCleanupPolicy> getCleanupPolicies() {
		return this.cleanupPolicies;
	}

	/**
	 * Adds a new instance of a CacheCleanupPolicy concrete implementation to the existent list of
	 * clean up policies assossiated with this cache manager.
	 *
	 * @param newValue The new instance of a CacheCleanupPolicy to be included as a cleanup mechanism.
	 */
	public void addCleanupPolicy(CacheCleanupPolicy newValue) {
		this.cleanupPolicies.add(newValue);
	}
	/**
	 * Setter method for the Notifier policy to be used with this cache.
	 *
	 * @param newValue An instance of a CacheNotifierPolicy to be used for this cache manager.
	 */
	public void setNotifierPolicy(CacheNotifierPolicy newValue) {
		this.notifierPolicy = newValue;
	}
	public CacheNotifierPolicy getNotifierPolicy() {
		return this.notifierPolicy;
	}

	/**
	 * Setter method for the loader policy of this cache.
	 *
	 * @param newValue An instance of a CacheLoaderPolicy  to be used with this cache.
	 */
	public void setLoaderPolicy(CacheLoaderPolicy newValue) {
		this.loaderPolicy = newValue;
	}
	public CacheLoaderPolicy getLoaderPolicy() {
		return this.loaderPolicy;
	}
	/**
	 * This method uses the list of CacheCleanup policies to delegate the cleanup mechanism of it's
	 * associated entity caches.
	 */
	public void cleanup() throws Exception {
		//Make sure we have something to cleanup
		if (this.getSize() > 0) {
			//Make sure we have some cleanup policies
			if (this.cleanupPolicies != null && !this.cleanupPolicies.isEmpty()) {
                for (CacheCleanupPolicy cleanupPolicy : this.cleanupPolicies) {
                    try {
                        lock.writeLock().lock();
                        cleanupPolicy.cleanup(this);
                    } catch (Exception e) {
                        lock.writeLock().unlock();
                    }
                }
			}
		}
	}

	/**
	 * This method delegates a notification for the installed nofitier cache mechanism.
	 */
	public void notifyCache(NotificationMessage message) {
		if (this.notifierPolicy != null) {
			this.notifierPolicy.notifyCache(message);
		}
	}

	/**
	 * Helper method to encapsulate possible cacheable entities on CachedEntities instances for
	 * internal cache manipulation.
	 *
	 * @param entities The collection on cacheable entities to be cached on this manager.
	 */
	protected void cacheEntities(Collection<Cacheable> entities) throws CacheException {
		if (entities != null) {
            for (Cacheable entity : entities) {
                this.register(entity);
            }
		}
	}
	/**
	 * Getter method for the grouped property.
	 */
	public boolean isGrouped() {
		return this.grouped;
	}
	/**
	 * Setter method for the grouped property.
	 *
	 * @param newValue The new value to be assigned to this property.
	 */
	public void setGrouped(boolean newValue) {
		this.grouped = newValue;
	}
	/**
	 * Getter method for the highWaterMark property.
	 */
	public long getHighWaterMark() {
		return highWaterMark;
	}
	/**
	 * Setter method for the highWaterMark property.
	 *
	 * @param highWaterMark The new value to be assigned to this property.
	 */
	public void setHighWaterMark(long highWaterMark) {
		this.highWaterMark = highWaterMark;
	}
	/**
	 * Getter method for the treshHold property.
	 */
	public long getTreshHold() {
		return treshHold;
	}
	/**
	 * Setter method for the treshHold property.
	 *
	 * @param treshHold The new value to be assigned to this property.
	 */
	public void setTreshHold(long treshHold) {
		this.treshHold = treshHold;
	}
	/**
	 * Getter method for the lowWaterMark property.
	 */
	public long getLowWaterMark() {
		return lowWaterMark;
	}
	/**
	 * Setter method for the lowWaterMark property.
	 *
	 * @param lowWaterMark The new value to be assigned to this property.
	 */
	public void setLowWaterMark(long lowWaterMark) {
		this.lowWaterMark = lowWaterMark;
	}
	/**
	 * Getter method for the distributable property.
	 */
	public boolean isDistributable() {
		return distributable;
	}
	/**
	 * Setter method for the distributable property.
	 *
	 * @param distributable The new value to be assigned to this property.
	 */
	public void setDistributable(boolean distributable) {
		this.distributable = distributable;
	}
	/**
	 * Getter method for the syncCluster property.
	 */
	public boolean isSyncCluster() {
		return syncCluster;
	}
	/**
	 * Setter method for the syncCluster property.
	 *
	 * @param syncCache The new value to be assigned to this property.
	 */
	public void setSyncCluster(boolean syncCache) {
		this.syncCluster = syncCache;
	}
	/**
	 * Getter method for the atomicGroup property.
	 */
	public boolean isAtomicGroup() {
		return atomicGroup;
	}
	/**
	 * Setter method for the atomicGroup property.
	 *
	 * @param atomicGroup The new value to be assigned to this property.
	 */
	public void setAtomicGroup(boolean atomicGroup) {
		this.atomicGroup = atomicGroup;
	}
	/**
	 * Getter method for the defaultIdleTime property.
	 */
	public long getDefaultIdleTime() {
		return defaultIdleTime;
	}
	/**
	 * Setter method for the defaultIdleTime property.
	 *
	 * @param defaultIdleTime The new value to be assigned to this property.
	 */
	public void setDefaultIdleTime(long defaultIdleTime) {
		this.defaultIdleTime = defaultIdleTime;
	}
	/**
	 * Getter method for the defaultTimeToLive property.
	 */
	public long getDefaultTimeToLive() {
		return defaultTimeToLive;
	}
	/**
	 * Setter method for the defaultTimeToLive property.
	 *
	 * @param defaultTimeToLive The new value to be assigned to this property.
	 */
	public void setDefaultTimeToLive(long defaultTimeToLive) {
		this.defaultTimeToLive = defaultTimeToLive;
	}
	/**
	 * Getter method for the missCounter property.
	 */
	public long getMissCounter() {
		return this.missCounter;
	}
	/**
	 * Getter method for the hitCounter property.
	 */
	public long getHitCounter() {
		return this.hitCounter;
	}
	/**
	 * Calculates the ratio of hits and misses (evaluate how well the cache is performing...)
	 * a value closer to 0 means that the cache is going very frequent to the persistence storage for the
	 * information. while a value closer to 1 means that the cache almost always have the information
	 * available ready for delivery.
	 *
	 * @return The ration of hits and misses.
	 */
	public double getHitRatio() {
		if (this.missCounter > 0 || this.hitCounter > 0) {
			double result = this.hitCounter;
			double totalHit = this.missCounter + this.hitCounter;
			result = result / totalHit;
			return result;
		}
		return 0;
	}

	public long getSize() throws Exception {
		long result = 0;
		if (this.groups != null && !this.groups.isEmpty()) {
            for (GroupCacheable groupCacheable : this.groups.values()) {
                result += groupCacheable.size();
            }
		}
		return result;
	}

	/**
	 * Getter method for the group property. This property is to be used by the CacheManager, the
	 * CacheFactory and possibly it's Policies. This property is not for any user exposure.
	 *
	 * @return The Collection of groups as a Map.
	 */
	public Map<Object, GroupCacheable>  getGroups() {
		return this.groups;
	}

	 void setMaxIdleTime(Object key, long newValue) {
		GroupCachedEntity aGroup = this.findGroupForEntity(key);
		if (aGroup != null) { //If available, great.. It's a hit.
			CachedEntity cachedEntity = aGroup.get(key);
			cachedEntity.setMaxIdleTime(newValue);
		}
	}
	 void setMaxTimeToLive(Object key, long newValue) {
		GroupCachedEntity aGroup = this.findGroupForEntity(key);
		if (aGroup != null) { //If available, great.. It's a hit.
			CachedEntity cachedEntity = aGroup.get(key);
			cachedEntity.setMaxTimeToLive(newValue);
		}
	}

    @Override
    protected void finalize() throws Throwable {
        for (CacheCleanupThread t:this.childThreads) {
            t.halt();
        }
        super.finalize();
    }
}
