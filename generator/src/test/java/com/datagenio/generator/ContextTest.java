package com.datagenio.generator;

import org.junit.Test;

import static org.junit.Assert.*;

public class ContextTest {

    @Test
    public void testGetOutputDirName() {
        var context = new Context("/tmp/test");
        assertEquals("/tmp/test", context.getOutputDirName());
    }

    @Test
    public void testIsVerboseDefault() {
        var context = new Context("/tmp/test");
        assertEquals(false, context.isVerbose());
    }

    @Test
    public void testIsVerbose() {
        var context = new Context("/tmp/test", true);
        assertEquals(true, context.isVerbose());
    }
}
