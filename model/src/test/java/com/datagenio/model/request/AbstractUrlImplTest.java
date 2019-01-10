package com.datagenio.model.request;

import java.util.ArrayList;

import com.datagenio.model.request.AbstractUrlImpl;
import com.datagenio.model.request.TypedParam;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class AbstractUrlImplTest {

    private AbstractUrlImpl url;

    @Before
    public void setUp() {
        this.url = new AbstractUrlImpl("base_url");
    }

    @Test
    public void testGetBaseUrl() {
        assertEquals("base_url", this.url.getBaseUrl());
    }

    @Test
    public void testSetBaseUrl() {
        this.url.setBaseUrl("new_base_url");
        assertEquals("new_base_url", this.url.getBaseUrl());
    }

    @Test
    public void testGetParams() {
        assertNotNull(this.url.getTypedParams());
    }

    @Test
    public void testSetParams() {
        ArrayList<TypedParam> params = new ArrayList<>();
        this.url.setTypedParams(params);
        assertEquals(params, this.url.getTypedParams());
    }

    @Test
    public void testAddParam() {
        TypedParam param = new TypedParam("q", "string");
        this.url.addTypedParam(param);
        assertTrue(this.url.getTypedParams().contains(param));
    }

    @Test
    public void testGetRequiredParams() {
        var requiredParam = new TypedParam("username", "string", true);
        var optionalParam = new TypedParam("password", "string", false);

        this.url.addTypedParam(requiredParam);
        this.url.addTypedParam(optionalParam);

        assertTrue(this.url.getRequiredParams().contains(requiredParam));
        assertFalse(this.url.getRequiredParams().contains(optionalParam));
    }

    @Test
    public void testEqualsSelf() {
        assertTrue(this.url.equals(this.url));
    }

    @Test
    public void testEqualsDiffBaseUrl() {
        AbstractUrlImpl other = new AbstractUrlImpl("some_other_url");
        assertFalse(this.url.equals(other));
    }

    @Test
    public void testEqualsDiffRequiredParams() {
        AbstractUrlImpl other = new AbstractUrlImpl(this.url.getBaseUrl());
        other.addTypedParam(new TypedParam("q", "string", true));
        assertFalse(this.url.equals(other));
    }

    @Test
    public void testEqualsDiffOptionalParams() {
        AbstractUrlImpl other = new AbstractUrlImpl(this.url.getBaseUrl());
        other.addTypedParam(new TypedParam("q", "string"));
        assertTrue(this.url.equals(other));
    }

    @Test
    public void testEqualsIdentical() {
        AbstractUrlImpl other = new AbstractUrlImpl(this.url.getBaseUrl());
        assertTrue(this.url.equals(other));
    }
}
