package com.datagenio.model;

import com.datagenio.model.api.AbstractHTTPRequest;
import com.datagenio.model.request.AbstractRequest;
import com.datagenio.model.request.AbstractUrlImpl;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class WebStateImplTest {

    private AbstractUrlImpl url;
    private WebStateImpl state;

    @Before
    public void setUp() {
        this.url = new AbstractUrlImpl("test_url");
        this.state = new WebStateImpl(this.url);
    }

    @Test
    public void testGetContext() {
        assertNotNull(this.state.getContext());
        assertEquals("test_url", this.state.getContext().getContextUrl().getBaseUrl());
    }

    @Test
    public void testSetContext() {
        StateContext context = new StateContext(new AbstractUrlImpl("other_url"));
        this.state.setContext(context);

        assertNotNull(this.state.getContext());
        assertEquals("other_url", this.state.getContext().getContextUrl().getBaseUrl());
    }

    @Test
    public void testGetUrl() {
        assertEquals(this.url, this.state.getUrl());
    }

    @Test
    public void testSetUrl() {
        AbstractUrlImpl newUrl = mock(AbstractUrlImpl.class);
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
        ArrayList<AbstractHTTPRequest> set = new ArrayList<>();
        this.state.setRequests(set);
        assertEquals(set, this.state.getRequests());
    }

    @Test
    public void testAddRequest() {
        AbstractRequest req = new AbstractRequest("GET", new AbstractUrlImpl("test_url"));
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
        WebStateImpl other = new WebStateImpl(new AbstractUrlImpl(this.state.getContext().getContextUrl().getBaseUrl()));
        assertTrue(this.state.equals(other));
    }

    @Test
    public void testEqualsDiffUrl() {
        WebStateImpl other = new WebStateImpl(new AbstractUrlImpl("some_new_url"));
        assertFalse(this.state.equals(other));
    }

    @Test
    public void testEqualsDiffRequestSet() {
        WebStateImpl other = new WebStateImpl(this.state.getContext().getContextUrl());
        other.addRequest(new AbstractRequest("GET", new AbstractUrlImpl("yet_another_url")));
        assertFalse(this.state.equals(other));
    }
}
