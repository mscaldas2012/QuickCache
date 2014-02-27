package com.msc.cache.mock;

import com.msc.cache.CacheException;
import com.msc.cache.Cacheable;
import com.msc.cache.loader.CacheLoaderPolicy;

import java.util.ArrayList;
import java.util.Collection;
import java.util.logging.Logger;

/**
 * This Mock class lazily initialized all lower case letters. and Initializes upper case letters on the fly.
 *
 *
 * This code was written by Marcelo Caldas.
 * e-Mail: mscaldas@gmail.com
 * <p/>
 * \* Project: QuickCache
 * <p/>
 * Date: 2/25/14
 * <p/>
 * Enjoy the details of life.
 */
public class TestAlphabetLoader implements CacheLoaderPolicy {
    private static final Logger logger = Logger.getLogger(TestAlphabetLoader.class.getName());

    @Override
    public Cacheable fetchEntity(Object cacheKey) throws CacheException {
        try {
            Character c = (Character)cacheKey;
            if ((c >= 'A' && c <= 'Z') || (c >= 'a' && c <= 'z' )) {
                return new Letter(c);
            }  else {
                throw new CacheException("Cache Key must be a char between 'a' and 'Z' for Alphabets!", "invalidKey");
            }
        } catch (ClassCastException e) {
            System.out.println("Cache Key must be an Char between 'a' and 'Z' for Alphabets!");
            throw new CacheException("Cache Key must be a char between 'a' and 'Z' for Alphabets!", "invalidKey");
        }
    }

    @Override
    public Collection<Cacheable> fetchAll() throws CacheException {
        Collection<Cacheable> alphabet = new ArrayList<Cacheable>();
        for (char l = 'a'; l <= 'z'; l++) {
            Letter lc  = new Letter(l);
            alphabet.add(lc);
        }
        return alphabet;
    }
}
