package com.msc.cache.instrumentation;

import com.msc.cache.CacheContract;
import com.msc.cache.CacheException;
import com.msc.cache.Cacheable;
import com.msc.cache.cleanup.CacheCleanupPolicy;
import com.msc.cache.initializer.CacheInitializerPolicy;
import com.msc.cache.loader.CacheLoaderPolicy;
import com.msc.cache.notifier.CacheNotifierPolicy;

import java.util.Collection;
import java.util.List;


/**
 *
 * @author <a href="mailto:mscaldas@gmail.com">Marcelo Caldas</a>
 */
public class CacheControl implements CacheControlMBean {
    private CacheContract contract = null;

    public CacheControl(CacheContract cacheManager) throws CacheException {
        this.contract = cacheManager;
    }
    private CacheContract getCacheManager() throws CacheException {
        return this.contract;
    }
    public void invalidate(Cacheable entity) throws Exception {
        this.contract.invalidate(entity);
    }

    public void refresh(Cacheable entity) throws Exception {
        this.contract.refresh(entity);
    }

    public Cacheable get(Object cacheKey) throws Exception {
        return this.contract.get(cacheKey);
    }

	public Cacheable peek(Object cacheKey) throws Exception {
		return this.contract.peek(cacheKey);
	}

	public Collection<Cacheable> getByGroup(Object groupKey) throws Exception {
        return contract.getByGroup(groupKey);
    }

    public Collection getAll() throws Exception {
        return this.contract.getAll();
    }

    public void flushGroup(Object groupKey) throws Exception {
        this.contract.flushGroup(groupKey);
    }

    public void flushAll() throws Exception {
        this.contract.flushAll();
    }

    public void setInitializerPolicy(CacheInitializerPolicy newValue) throws Exception {
        //TODO:: Check repercursion and perform cleanup
        this.getCacheManager().setInitializerPolicy(newValue);
    }

    public void setCleanupPolicies(List newValue) throws Exception {
        //TODO:: Check repercursion and perform cleanup
        this.getCacheManager().setCleanupPolicies(newValue);
    }

    public void addCleanupPolicy(CacheCleanupPolicy newValue) throws Exception {
        //TODO:: Check repercursion and perform cleanup
        this.getCacheManager().addCleanupPolicy(newValue);
    }

    public void setNotifierPolicy(CacheNotifierPolicy newValue) throws Exception {
        //TODO:: Check repercursion and perform cleanup
        this.getCacheManager().setNotifierPolicy(newValue);
    }

    public void setLoaderPolicy(CacheLoaderPolicy newValue) throws Exception {
        //TODO:: Check repercursion and perform cleanup
        this.getCacheManager().setLoaderPolicy(newValue);
    }

    public void cleanup() throws Exception {
        //TODO:: Check repercursion and perform cleanup
        this.getCacheManager().cleanup();
    }

    public boolean isGrouped() throws Exception {
        return this.getCacheManager().isGrouped();
    }

    public void setGrouped(boolean newValue) throws Exception {
        //TODO:: Check repercursion and perform cleanup
        this.getCacheManager().setGrouped(newValue);
    }

    public long getHighWaterMark() throws Exception {
        return this.getCacheManager().getHighWaterMark();
    }

    public void setHighWaterMark(long highWaterMark) throws Exception {
        //TODO:: Check repercursion and perform cleanup
        this.getCacheManager().setHighWaterMark(highWaterMark);
    }

    public long getTreshHold() throws Exception {
        return this.getCacheManager().getTreshHold();
    }

    public void setTreshHold(long treshHold) throws Exception {
        //TODO:: Check repercursion and perform cleanup
        this.getCacheManager().setTreshHold(treshHold);
    }

    public long getLowWaterMark() throws Exception {
        return this.getCacheManager().getLowWaterMark();
    }

    public void setLowWaterMark(long lowWaterMark) throws Exception {
        //TODO:: Check repercursion and perform cleanup
        this.getCacheManager().setLowWaterMark(lowWaterMark);
    }

    public boolean isDistributable() throws Exception {
        return this.getCacheManager().isDistributable();
    }

    public void setDistributable(boolean distributable) throws Exception {
        //TODO:: Check repercursion and perform cleanup
        this.getCacheManager().setDistributable(distributable);
    }

    public boolean isSyncCluster() throws Exception {
        return this.getCacheManager().isSyncCluster();
    }

    public void setSyncCluster(boolean syncCache) throws Exception {
        //TODO:: Check repercursion and perform cleanup
        this.getCacheManager().setSyncCluster(syncCache);

    }

    public boolean isAtomicGroup() throws Exception {
        return this.getCacheManager().isAtomicGroup();
    }

    public void setAtomicGroup(boolean atomicGroup) throws Exception {
        //TODO:: Check repercursion and perform cleanup
        this.getCacheManager().setAtomicGroup(atomicGroup);
    }

    public long getDefaultIdleTime() throws Exception {
        return this.getCacheManager().getDefaultIdleTime();
    }

    public void setDefaultIdleTime(long defaultIdleTime) throws Exception {
        //TODO:: Check repercursion and perform cleanup
        this.getCacheManager().setDefaultIdleTime(defaultIdleTime);

    }

    public long getDefaultTimeToLive() throws Exception {
        return this.getCacheManager().getDefaultTimeToLive();
    }

    public void setDefaultTimeToLive(long defaultTimeToLive) throws Exception {
        //TODO:: Check repercursion and perform cleanup
        this.getCacheManager().setDefaultTimeToLive(defaultTimeToLive);
    }

    public long getMissCounter() throws Exception {
        return this.getCacheManager().getMissCounter();
    }

    public long getHitCounter() throws Exception {
        return this.getCacheManager().getHitCounter();
    }

    public double getHitRatio() throws Exception {
        return this.getCacheManager().getHitRatio();
    }

    public long getSize() throws Exception {
        return this.getCacheManager().getSize();
    }

    public CacheInitializerPolicy getInitializerPolicy() throws Exception {
        return this.getCacheManager().getInitializerPolicy();
    }

    public List getCleanupPolicies() throws Exception {
        return this.getCacheManager().getCleanupPolicies();
    }

    public CacheNotifierPolicy getNotifierPolicy() throws Exception {
        return this.getCacheManager().getNotifierPolicy();
    }

    public CacheLoaderPolicy getLoaderPolicy() throws Exception {
        return this.getCacheManager().getLoaderPolicy();
    }

    public void setCleanupFrequency(long newValue) {
        //return this.
    }

    public long getCleanupFrequency() {
        return 0;
    }

}
