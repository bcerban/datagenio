package com.datagenio.model.util;

import org.apache.http.HttpRequest;
import org.apache.http.ProtocolVersion;
import org.apache.http.RequestLine;
import org.apache.http.message.BasicHttpRequest;
import org.apache.http.message.BasicRequestLine;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class SimpleRequestAbstractorTest {
    private RequestLine requestLine;
    private HttpRequest request;
    private SimpleRequestAbstractor simpleRequestAbstractor;

    @Before
    public void setUp() {
        this.requestLine = new BasicRequestLine(
                "GET",
                "http://test.com",
                new ProtocolVersion("HTTP", 1, 1)
        );
        this.request = new BasicHttpRequest(this.requestLine);
        this.simpleRequestAbstractor = new SimpleRequestAbstractor();
    }

    @Test
    public void testProcess() {
        var abstractRequest = this.simpleRequestAbstractor.process(this.request);
        assertEquals("GET", abstractRequest.getMethod());
        assertEquals("http://test.com", abstractRequest.getUrl().getBaseUrl());
    }
}
