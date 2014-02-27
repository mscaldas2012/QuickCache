package com.msc.cache.notifier;

/**
 * Contract for possible notifiers implementations.
 * Different types of notification can be implemented:
 * <UL>
 * <LI>Notify using JMS or JavaGroups, or any other broadcast mechanism.</LI>
 * <LI>Notification should happen synchronous or assynchronously</LI>
 * <LI>You can send a message to refresh caches from the Message itself or from the DB</LI>
 * </UL>
 *
 * <P>One issue that I'm struggling is how to desing this contract. I see two possible implementations:<BR>
 * 1.) Have a generic method that can receive the message and a Object in question.<BR>
 * 2.) Have one method for each possible notification.</P>
 *
 * <P>The first approach is much more generic and doesn't restrict the number of notifications that can
 * be sent from the CacheManager. But every implementation will have to factor the message parameter to
 * check the appropriate behavior they have to execute.</P>
 * <P>On the second approach we're limiting the different notifications by making the contract more
 * restricted. Some descendants will not want to implement all types of notification (but that can
 * be solved with an abstract Adapter); </P>
 *
 *
 *
 * @author <a href="mailto:mscaldas@gmail.com">Marcelo Caldas</a>
 */
public interface CacheNotifierPolicy {
	public void notifyCache(NotificationMessage message);
}
