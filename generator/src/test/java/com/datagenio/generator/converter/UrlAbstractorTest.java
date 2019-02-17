package com.datagenio.generator.converter;

import com.datagenio.databank.api.InputBuilder;
import com.datagenio.model.request.AbstractUrl;
import org.junit.Before;
import org.junit.Test;

import java.net.URI;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class UrlAbstractorTest {

    private UrlAbstractor abstractor;
    private InputBuilder inputBuilder;

    @Before
    public void setUp() {
        inputBuilder = mock(InputBuilder.class);
        abstractor = new UrlAbstractor(inputBuilder);
    }

    @Test
    public void testProcessUrlWithoutParams() {
        String url = "http://test.com";
        URI uri = URI.create(url);

        AbstractUrl processedUrl = abstractor.process(uri);
        assertEquals(url, processedUrl.getBaseUrl());
    }
}
