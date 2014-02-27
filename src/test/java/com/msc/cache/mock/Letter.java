package com.msc.cache.mock;

import com.msc.cache.Cacheable;

import java.util.logging.Logger;

/**
 * This code was written by Marcelo Caldas.
 * e-Mail: mscaldas@gmail.com
 * <p/>
 * \* Project: QuickCache
 * <p/>
 * Date: 2/26/14
 * <p/>
 * Enjoy the details of life.
 */
public class Letter implements Cacheable {
    private static final Logger logger = Logger.getLogger(Letter.class.getName());

    private Character letter;

    @Override
    public Object getCacheKey() {
        return this.letter;
    }

    public Letter(char letter) {
        this.letter = letter;
    }

    public char getLetter() {
        return letter;
    }

    public void setLetter(char letter) {
        this.letter = letter;
    }

    @Override
    public String toString() {
        return String.valueOf(this.letter);
    }
}
