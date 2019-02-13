package com.datagenio.model;

import com.datagenio.model.request.AbstractRequest;
import com.datagenio.model.request.AbstractUrl;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class WebStateTest {

    private AbstractUrl url;
    private WebState state;

    @Before
    public void setUp() {
        this.url = new AbstractUrl("test_url");
        this.state = new WebState(this.url);
    }

    @Test
    public void testGetUrl() {
        assertEquals(this.url, this.state.getUrl());
    }

    @Test
    public void testSetUrl() {
        AbstractUrl newUrl = mock(AbstractUrl.class);
        this.state.setUrl(newUrl);
        assertEquals(newUrl, this.state.getUrl());
    }

    @Test
    public void testGetRequestSet() {
        assertNotNull(this.state.getRequests());
        assertEquals(0, this.state.getRequests().size());
    }

    @Test
    public void testSetRequestSet() {
        ArrayList<AbstractRequest> set = new ArrayList<>();
        this.state.setRequests(set);
        assertEquals(set, this.state.getRequests());
    }

    @Test
    public void testAddRequest() {
        AbstractRequest req = new AbstractRequest("GET", new AbstractUrl("test_url"));
        this.state.addRequest(req);
        assertEquals(1, this.state.getRequests().size());
        assertTrue(this.state.getRequests().contains(req));
    }

    @Test
    public void testEqualsSelf() {
        assertTrue(this.state.equals(this.state));
    }

    @Test
    public void testEqualsIdentical() {
        WebState other = new WebState(new AbstractUrl(this.state.getUrl().getBaseUrl()));
        assertTrue(this.state.equals(other));
    }

    @Test
    public void testEqualsDiffUrl() {
        WebState other = new WebState(new AbstractUrl("some_new_url"));
        assertFalse(this.state.equals(other));
    }

    @Test
    public void testEqualsDiffRequestSet() {
        WebState other = new WebState(this.state.getUrl());
        other.addRequest(new AbstractRequest("GET", new AbstractUrl("yet_another_url")));
        assertFalse(this.state.equals(other));
    }
}
