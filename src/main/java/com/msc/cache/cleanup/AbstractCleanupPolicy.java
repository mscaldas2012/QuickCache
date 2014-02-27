package com.msc.cache.cleanup;

import java.util.logging.Logger;

/**
 * This code was written by Marcelo Caldas.
 * e-Mail: mscaldas@gmail.com
 * <p/>
 * \* Project: QuickCache
 * <p/>
 * Date: 2/25/14
 * <p/>
 * Enjoy the details of life.
 */
public abstract class AbstractCleanupPolicy implements CacheCleanupPolicy {
    private static final Logger logger = Logger.getLogger(AbstractCleanupPolicy.class.getName());
    private int frequency;

    public int getFrequency() {
        return frequency;
    }

    public void setFrequency(int frequency) {
        this.frequency = frequency;
    }
}
