package com.msc.cache.cleanup;

import com.msc.cache.CacheManager;
import com.msc.cache.CachedEntity;
import com.msc.cache.GroupCachedEntity;

import java.util.Iterator;

/**
 * <P>This Cleanup mechanism takes care of expired Entities. An entity can be expired in two ways:
 * <OL>
 * <LI>By a TimeToLive: an instance is capable of living for so long.</LI>
 * <LI>By a idleTime: an instance is able to be idle for so long. After that, it is expired.</LI>
 * </OL>
 *
 * This Cleanup mechanism is automatically set up if both defaultIdleTime and defaultTimeToLive
 * properties at the cache-config.xml configuration file have values greater than 0.
 *
 * @author <a href="mailto:mscaldas@gmail.com">Marcelo Caldas</a>
 */
public class ExpiredCleanupPolicy extends AbstractCleanupPolicy {

	public ExpiredCleanupPolicy() {
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
	                        cachedEntity = (CachedEntity) cachedEntityIT.next();
                            long currentIdleTime = ((currentTime - cachedEntity.getLastAccessedTime()) / 1000);
                            long currentAge = (currentTime - cachedEntity.getCreationTime())/1000;
                            if ((cachedEntity.getMaxIdleTime() < currentIdleTime) ||
                                (cachedEntity.getMaxTimeToLive() < currentAge)) {
                                if (group.isAtomicGroup()) {
                                    groupsIT.remove();
                                    break;
                                } else {
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
