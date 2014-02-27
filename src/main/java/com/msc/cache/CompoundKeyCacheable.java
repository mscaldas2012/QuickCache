package com.msc.cache;

/**
 * <P>Whenever we need to cache an entity that can be referenced by different sets
 * of keys, it must implement this method to provide all possible keys available for
 * that entity.
 * </P>
 * <p/>
 * <P>Date: Sep 1, 2005 - 12:43:51 PM</P>
 *
 * @author <a href="mailto:mscaldas@gmail.com">Marcelo Caldas</a>
 */
public interface CompoundKeyCacheable {
	/**
	 * <P>Returns all available keys used by a Cacheable entity.</P>
	 *
	 * <P><B>Note:</b> It is mandatory that the array returned must be always the same size, with the
	 * equivalent keys under the same position. Also, use most common keys on the beggining of the
	 * array since the array will be queried from pos 0 to the last position.</P>
	 *
	 * @return
	 */
	public Object[] getSecondaryKeys();
}
