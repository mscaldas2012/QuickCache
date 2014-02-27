package com.msc.cache;


import java.util.logging.Logger;

/**
 * This class will fire a thread for each CacheManager.  This thread is responsible for calling
 * the cleanup mechanism of each cacheManager for cleanup purposes.
 *
 * @author <a href="mailto:mscaldas@gmail.com">Marcelo Caldas</a>
 */
public class CacheCleanupThread extends Thread {
    /**
     * Flag to indicate whether the Thread should keep running or stop.
     * Replacement for the deprecated stop() method on Thread class.
     */
    private boolean shouldKeepRunning = true;
    /** The time that this thread should sleep until the next kick off of clean up threads */
    private long timeToSleep ;
    /** The Manager that this thread is being associated with. */
    private com.msc.cache.CacheContract cacheManager;

	private static Logger logger = Logger.getLogger(CacheCleanupThread.class.getName());
    /**
     * Constructor that applies necessary values for the mandatory fields - timeToLive and cacheManager.
     */
    public CacheCleanupThread(int timeToSleep, CacheContract cacheManager) {
        super("CacheCleanupThread");
        setPriority(Thread.MIN_PRIORITY);
        this.timeToSleep = timeToSleep;
        this.cacheManager = cacheManager;
    }

    /**
     * Sustitution of the stop method that has been deprecated on the Thread class.
     * This method follows the suggestion on the Javadocs and uses the shouldKeepRunning flag
     * to indicate whether the Thread should run or stop running...
     */
    public synchronized void halt() {
        this.shouldKeepRunning = false;
    }

    /**
     * The main method of this class. Responsible for starting the cleanup policies of each Manager.
     */
    public void run() {
        while (this.shouldKeepRunning) {
            try {
                Thread.sleep(this.timeToSleep);
            } catch (InterruptedException ignored) {
				logger.finest("interrupted exception raised while sleeping thread: " + this.toString() + "\nError: " + ignored.getMessage());
            }
            //expire objects
            try {
                this.cacheManager.cleanup();
            } catch (Exception e) {
                e.printStackTrace();  //To change body of catch statement use Options | File Templates.
            }
        }
    }

//    private synchronized boolean shouldKeepRunning() {
//        return this.shouldKeepRunning;
//    }
}
