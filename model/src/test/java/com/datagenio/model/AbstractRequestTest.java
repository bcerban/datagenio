package com.datagenio.model;

import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;

import static org.junit.Assert.*;

public class AbstractRequestTest {
    private AbstractRequest request;

    @Before
    public void setUp() {
        AbstractUrl url = new AbstractUrl("some/test/url");
        this.request = new AbstractRequest("GET", url);
    }

    @Test
    public void testGetMethod() {
        assertEquals("GET", this.request.getMethod());
    }

    @Test
    public void testSetMethod() {
        this.request.setMethod("POST");
        assertEquals("POST", this.request.getMethod());
    }

    @Test
    public void testGetRequestUrl() {
        assertNotNull(this.request.getRequestUrl());
        assertEquals("some/test/url", this.request.getRequestUrl().getBaseUrl());
    }

    @Test
    public void testSetRequestUrl() {
        AbstractUrl url = new AbstractUrl("some/other/url");
        this.request.setRequestUrl(url);
        assertEquals(url, this.request.getRequestUrl());
    }

    @Test
    public void testGetRequestBody() {
        assertNull(this.request.getRequestBody());
    }

    @Test
    public void testSetRequestBody() {
        AbstractBody body = new AbstractBody();
        this.request.setRequestBody(body);
        assertEquals(body, this.request.getRequestBody());
    }

    @Test
    public void testGetHeaders() {
        assertNotNull(this.request.getHeaders());
        assertEquals(0, this.request.getHeaders().size());
    }

    @Test
    public void testSetHeaders() {
        HashMap<String, String> headers = new HashMap<>();
        headers.put("Content-type", "application/json");
        this.request.setHeaders(headers);
        assertEquals(headers, this.request.getHeaders());
        assertEquals(1, this.request.getHeaders().size());
    }

    @Test
    public void testAddHeader() {
        this.request.addHeader("Content-type", "text/html");
        assertTrue(this.request.getHeaders().containsKey("Content-type"));
        assertEquals("text/html", this.request.getHeaders().get("Content-type"));
    }

    @Test
    public void testGetSession() {
        assertNull(this.request.getSession());
    }

    @Test
    public void testSetSession() {
        Session session = new Session();
        this.request.setSession(session);
        assertEquals(session, this.request.getSession());
    }
}
