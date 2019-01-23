package com.datagenio.generator.converter;

import com.datagenio.model.api.AbstractUrl;
import org.junit.Before;
import org.junit.Test;

import java.net.URI;

import static org.junit.Assert.*;

public class UrlAbstractorTest {

    private UrlAbstractor abstractor;

    @Before
    public void setUp() {
        abstractor = new UrlAbstractor();
    }

    @Test
    public void testProcessUrlWithoutParams() {
        String url = "http://test.com";
        URI uri = URI.create(url);

        AbstractUrl processedUrl = abstractor.process(uri);
        assertEquals(url, processedUrl.getBaseUrl());
    }
}
