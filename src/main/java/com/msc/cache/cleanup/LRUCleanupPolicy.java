package com.msc.cache.cleanup;

import com.msc.cache.CacheManager;
import com.msc.cache.CachedEntity;
import com.msc.cache.GroupCachedEntity;

import java.util.Iterator;

/**
 * Created by IntelliJ IDEA.
 * @author <a href="mailto:mscaldas@gmail.comm">Marcelo Caldas</a>
 */
public class LRUCleanupPolicy extends AbstractCleanupPolicy {

	public LRUCleanupPolicy() {
	}

	public void cleanup(CacheManager cacheManager) {
		//Go thru all Groups...
		Iterator groupsIT = cacheManager.getGroups().entrySet().iterator();
		GroupCachedEntity group;
		long currentTime = System.currentTimeMillis();
		while  (groupsIT.hasNext()) {
			group = (GroupCachedEntity) groupsIT.next();
			if (group.size() > 0) {
				//Go thru all entities...
				Iterator cachedEntityIT = group.iterator();
				CachedEntity cachedEntity = null;
				while (cachedEntityIT.hasNext()) {
					cachedEntity = (CachedEntity) cachedEntityIT.next();
				}
			}
		}
	}

}
