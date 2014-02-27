package com.msc.cache;

import com.msc.cache.mock.Letter;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Collection;
import java.util.logging.Logger;

import static org.junit.Assert.assertTrue;

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
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:test-context.xml")
public class CacheManagerTest {
    private static final Logger logger = Logger.getLogger(CacheManagerTest.class.getName());

    @Autowired
    private CacheManager cm;

    @org.junit.Before
    public void setUp() throws Exception {

    }

    @org.junit.Test
    public void testInvalidate() throws Exception {

    }

    @org.junit.Test
    public void testRefresh() throws Exception {

    }

    @org.junit.Test
    public void testGet() throws Exception {
//        for (int i = 0; i < 1000; i++) {
        Letter lm = (Letter) cm.get('m');
        System.out.println("lm.getLetter() = " + lm);
        assertTrue(cm.getHitCounter() == 1);
        System.out.println("cm.getHitCounter() = " + cm.getHitCounter());
        System.out.println("cm.getMissCounter() = " + cm.getMissCounter());
        Letter upper = (Letter) cm.get('M');
        System.out.println("upper = " + upper);
        System.out.println("cm.getHitCounter() = " + cm.getHitCounter());
        System.out.println("cm.getMissCounter() = " + cm.getMissCounter());
        try {
            cm.get('8');
        } catch(CacheException e) {
            System.out.println("Exception properly thrown: " + e.getMessage());
        }
        System.out.println("cm.getHitCounter() = " + cm.getHitCounter());
        System.out.println("cm.getMissCounter() = " + cm.getMissCounter());
        }
//        System.out.println("cm.getHitCounter() = " + cm.getHitCounter());
//        System.out.println("cm.getMissCounter() = " + cm.getMissCounter());
//    }



    @org.junit.Test
    public void testPeek() throws Exception {
        Letter l = (Letter) cm.peek('b');
        System.out.println("l.getLetter() = " + l);
        System.out.println("cm.getHitCounter() = " + cm.getHitCounter());
        assertTrue(cm.getHitCounter() == 0);

    }

    @org.junit.Test
    public void testGetByGroup() throws Exception {

    }

    @org.junit.Test
    public void testGetAll() throws Exception {
        Collection<Cacheable> alphabet =  cm.getAll();
        System.out.println("alphabet.size() = " + alphabet.size());
        System.out.println("cm.getSize() = " + cm.getSize());
        assertTrue(alphabet.size() == cm.getSize());
        for (Cacheable l: alphabet) {
            System.out.print(l + ", ");
        }

    }

    @org.junit.Test
    public void testFlushGroup() throws Exception {

    }

    @org.junit.Test
    public void testFlushAll() throws Exception {

    }
}
