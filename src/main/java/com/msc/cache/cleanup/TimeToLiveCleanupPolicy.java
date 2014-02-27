package com.msc.cache.cleanup;

import com.msc.cache.CacheManager;
import com.msc.cache.CachedEntity;
import com.msc.cache.GroupCachedEntity;

import java.util.Iterator;

/**
 * <P>This Cleanup mechanism takes care of expired Entities. An entity can be expired by
 * timeToLive: an instance is able to live for so long. After that, it is expired.</P>
 *
 * <p>This Cleanup mechanism is automatically set up if defaultTimeToLive.
 * property at the cache-config.xml configuration file have value greater than 0.</P>
 *
 * @author <a href="mailto:mscaldas@gmail.com">Marcelo Caldas</a>
 */
public class TimeToLiveCleanupPolicy extends AbstractCleanupPolicy {

    public TimeToLiveCleanupPolicy() {

	}

	public void cleanup(CacheManager cacheManager) {
        //Go thru all Groups...
        //TODO::FIx potential NPE
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
                            long currentAge = (currentTime - cachedEntity.getCreationTime())/1000;
                            if (cachedEntity.getMaxTimeToLive() < currentAge) {
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
