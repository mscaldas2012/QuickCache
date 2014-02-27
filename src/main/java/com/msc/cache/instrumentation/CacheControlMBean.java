package com.msc.cache.instrumentation;

import com.msc.cache.CacheContract;
/**
 *
 * @author <a href="mailto:mscaldas@gmail.com">Marcelo Caldas</a>
 */
public interface CacheControlMBean extends CacheContract {
    public void setCleanupFrequency(long newValue);
    public long getCleanupFrequency();
}
