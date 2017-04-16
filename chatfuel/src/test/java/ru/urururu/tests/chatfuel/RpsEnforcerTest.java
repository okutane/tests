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
}