package com.datagenio.context;

import org.junit.Test;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class ContextTest {
    private static String ROOT_URL = "http://test.com/";
    private static String OUTPUT_DIR = "/tmp/test";

    @Test
    public void testGetConfiguration() throws DatagenioException {
        var context = new Context();
        context.setRootUrl(ROOT_URL);
        context.setOutputDirName(OUTPUT_DIR);
        var config = context.getConfiguration();

        assertNotNull(config);
        assertNotNull(config.getConnectionMode());
        assertNotNull(config.getRequestSaveMode());
        assertEquals(ROOT_URL, config.getRootUrl());
        assertEquals(OUTPUT_DIR, config.getOutputDirName());
    }
}
