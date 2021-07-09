package test;

import main.*;
import org.junit.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;


public class TestDehnTwist {

    @Test
    public void testDehnTwistConstructers() {
        DehnTwist t = new DehnTwist(1, 4, 2);
        DehnTwist s = new DehnTwist(1, 4, 3);
        s = new DehnTwist(s.getidentifier(), 2);
        assertEquals(t.getExp(), s.getExp());
    }

    @Test
    public void testDehnTwistConstructorPairOverflow() {
        DehnTwist t = new DehnTwist(5, 4, 2);
        DehnTwist s = new DehnTwist(1, 4, 3);
        s = new DehnTwist(s.getidentifier(), 2);
        assertEquals(t.getExp(), s.getExp());
    }

    @Test
    public void testDehnTwistToString() {
        DehnTwist t = new DehnTwist(1, 4, 2, 4);
        assertEquals("D(1, 4)^2", t.toString());
    }

    @Test
    public void testDehnTwistToStringNickname() {
        DehnTwist t = new DehnTwist(1, 4, 2);
        assertEquals("D(x)^2", t.toString());

        t = new DehnTwist(2, 4, -3);
        assertEquals("D(c)^-3", t.toString());

    }

    @Test
    public void testIsLiftable() {
        DehnTwist t = new DehnTwist(1, 4, 5);
        assertFalse(t.isLiftable());
        t = new DehnTwist(1, 3, 2);
        assertTrue(t.isLiftable());
        t = new DehnTwist(6, 3, 2);
        assertTrue(t.isLiftable());
        t = new DehnTwist(8, 6, 1);
        assertFalse(t.isLiftable());
    }

    @Test
    public void testCommute() {
        // TODO: write a commutativity checker
        DehnTwist t = new DehnTwist(1, 2, 2);
        DehnTwist s = new DehnTwist(6, 5, 3);
        assertTrue(t.commutesWith(s));
        s = new DehnTwist(3, 4, 1);
        assertTrue(t.commutesWith(s));
        s = new DehnTwist(2, 4, 1);
        assertFalse(t.commutesWith(s));
        t = new DehnTwist(1, 3, -3);
        assertFalse(s.commutesWith(t));
    }

    @Test
    public void testEquals() {
        DehnTwist t = new DehnTwist(1, 2, 2);
        DehnTwist s = new DehnTwist(6, 5, 3);
        s = new DehnTwist(s.getidentifier(), 2);
        assertEquals(t, s);
    }
}