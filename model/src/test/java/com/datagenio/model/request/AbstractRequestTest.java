package com.datagenio.model.request;

import com.datagenio.model.Session;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collection;

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
        assertNotNull(this.request.getUrl());
        assertEquals("some/test/url", this.request.getUrl().getBaseUrl());
    }

    @Test
    public void testSetRequestUrl() {
        AbstractUrl url = new AbstractUrl("some/other/url");
        this.request.setUrl(url);
        assertEquals(url, this.request.getUrl());
    }

    @Test
    public void testGetRequestBody() {
        assertNull(this.request.getBody());
    }

    @Test
    public void testSetRequestBody() {
        AbstractBody body = new AbstractBody();
        this.request.setBody(body);
        assertEquals(body, this.request.getBody());
    }

    @Test
    public void testGetHeaders() {
        assertNotNull(this.request.getHeaders());
        assertEquals(0, this.request.getHeaders().size());
    }

    @Test
    public void testSetHeaders() {
        Collection<HttpHeader> headers = new ArrayList<>();
        headers.add(new HttpHeader("Content-type", "application/json"));
        this.request.setHeaders(headers);
        assertEquals(headers, this.request.getHeaders());
        assertEquals(1, this.request.getHeaders().size());
    }

    @Test
    public void testAddHeader() {
        var header = new HttpHeader("Content-type", "text/html");
        this.request.addHeader(header);
        assertTrue(this.request.getHeaders().contains(header));
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

    @Test
    public void testEqualsSelf() {
        assertTrue(this.request.equals(this.request));
    }

    @Test
    public void testEqualsIdentical() {
        AbstractRequest other = new AbstractRequest(this.request.getMethod(), this.request.getUrl());
        other.setHeaders(this.request.getHeaders());
        other.setBody(this.request.getBody());
        assertTrue(this.request.equals(other));
    }

    @Test
    public void testEqualsDiffMethod() {
        AbstractRequest other = new AbstractRequest("POST", this.request.getUrl());
        other.setHeaders(this.request.getHeaders());
        other.setBody(this.request.getBody());
        assertFalse(this.request.equals(other));
    }

    @Test
    public void testEqualsDiffUrl() {
        AbstractRequest other = new AbstractRequest(this.request.getMethod(), new AbstractUrl("new_base_url"));
        other.setHeaders(this.request.getHeaders());
        other.setBody(this.request.getBody());
        assertFalse(this.request.equals(other));
    }

    @Test
    public void testEqualsDiffHeaders() {
        Collection<HttpHeader> headers = new ArrayList<>();
        headers.add(new HttpHeader("Content-type", "application/json"));

        AbstractRequest other = new AbstractRequest("POST", this.request.getUrl());
        other.setHeaders(headers);
        other.setBody(this.request.getBody());
        assertFalse(this.request.equals(other));
    }

    @Test
    public void testEqualsDiffBody() {
        AbstractBody body = new AbstractBody();
        body.addProperty(new TypedParam("product", AbstractBodyTest.OBJECT));

        AbstractRequest other = new AbstractRequest("POST", this.request.getUrl());
        other.setHeaders(this.request.getHeaders());
        other.setBody(body);
        assertFalse(this.request.equals(other));
    }
}
