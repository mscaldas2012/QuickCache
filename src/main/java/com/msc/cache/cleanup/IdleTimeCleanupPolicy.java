package com.msc.cache.cleanup;

import com.msc.cache.CacheManager;
import com.msc.cache.CachedEntity;
import com.msc.cache.GroupCachedEntity;

import java.util.Iterator;
import java.util.logging.Logger;

/**
 * <P>This Cleanup mechanism takes care of expired Entities. An entity can be expired by
 * idleTime: an instance is able to be idle for so long. After that, it is expired.</P>
 *
 * <p>This Cleanup mechanism is automatically set up if defaultIdleTime
 * property at the cache-config.xml configuration file have value greater than 0.</P>
 *
 * @author <a href="mailto:mscaldas@gmail.com">Marcelo Caldas</a>
 */                                      	  
public class IdleTimeCleanupPolicy extends AbstractCleanupPolicy {
	private Logger logger = Logger.getLogger(IdleTimeCleanupPolicy.class.getName());

    public IdleTimeCleanupPolicy() {
    }

    public void cleanup(CacheManager cacheManager) {
        //Go thru all Groups...
        Iterator groupsIT = cacheManager.getGroups().values().iterator();
        if (groupsIT != null) {
            GroupCachedEntity group;
            long currentTime = System.currentTimeMillis();
            while (groupsIT.hasNext()) {
                group = (GroupCachedEntity) groupsIT.next();
                if (group.size() > 0) {
                    //Go thru all entities...
                    Iterator cachedEntityIT = group.iterator();
                    if (cachedEntityIT != null) {
                        CachedEntity cachedEntity;
                        while (cachedEntityIT.hasNext()) {
	                        cachedEntity  = (CachedEntity) cachedEntityIT.next();
                            long currentIdleTime = ((currentTime - cachedEntity.getLastAccessedTime()) / 1000);
                            if (cachedEntity.getMaxIdleTime() < currentIdleTime) {
                                if (group.isAtomicGroup()) {
                                    logger.finest("Removing Group...");
                                    groupsIT.remove();
                                    break;
                                } else {
                                    logger.finest("Removing Cached Entity...");
                                    cachedEntityIT.remove();
                                }
                            }
                        }
                    }
                }
            }
        }
    }

}
