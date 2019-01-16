package com.datagenio.crawler.util;

import com.datagenio.crawler.api.EventableExtractor;
import org.junit.Test;
import static org.junit.Assert.*;

public class EventExtractorFactoryTest {

    @Test
    public void testGet() {
        var extractor = EventExtractorFactory.get();
        assertTrue(extractor instanceof EventableExtractor);
        assertFalse(extractor.getRules().isEmpty());
    }
}
