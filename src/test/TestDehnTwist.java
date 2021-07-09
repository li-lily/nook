package test;

import main.*;
import org.junit.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class TestDehnTwist {

    @Test
    public void testDehnTwistConstructers() {
        DehnTwist t = new DehnTwist(1, 4, 2);
        DehnTwist s = new DehnTwist(1, 4, 3);
        s = new DehnTwist(s.getidentifier(), 2);
        assertEquals(t.getExp(), s.getExp());
    }

    @Test
    public void testDehnTwistToString() {
        DehnTwist t = new DehnTwist(1, 4, 2);
        assertEquals("D(1, 4)^2", t.toString());
    }
}