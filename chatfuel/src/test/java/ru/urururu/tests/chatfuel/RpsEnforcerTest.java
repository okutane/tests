package ru.urururu.tests.chatfuel;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * @author <a href="mailto:dmitriy.g.matveev@gmail.com">Dmitry Matveev</a>
 */
public class RpsEnforcerTest {
    @Test
    public void testExactRps() {
        RpsEnforcer enforcer = new RpsEnforcer(1);

        assertEquals(true, enforcer.allowed(1000)); // 1st request ever
        assertEquals(false, enforcer.allowed(1999)); // 2nd request per 1st second
        assertEquals(true, enforcer.allowed(2000)); // next allowed request
    }

    @Test
    public void testSecondBoundary() {
        RpsEnforcer enforcer = new RpsEnforcer(1);

        assertEquals(true, enforcer.allowed(1999)); // 1st request ever
        assertEquals(false, enforcer.allowed(2000)); // have to wait 999 more ms
    }

    @Test
    public void testSecondBoundaryBig() {
        RpsEnforcer enforcer = new RpsEnforcer(1000);

        for (int i = 0; i < 1000; i++) {
            assertEquals(true, enforcer.allowed(1999)); // requests at the end of second
        }

        for (int i = 0; i < 1000; i++) {
            assertEquals(false, enforcer.allowed(2000)); // requests at the beginning of next second
        }

        assertEquals(false, enforcer.allowed(2998)); // still can't
        assertEquals(true, enforcer.allowed(2999)); // can again
    }
}