package com.datagenio.model;

import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class AbstractUrlTest {

    private AbstractUrl url;

    @Before
    public void setUp() {
        this.url = new AbstractUrl("base_url");
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
        assertNotNull(this.url.getParams());
    }

    @Test
    public void testSetParams() {
        ArrayList<TypedParam> params = new ArrayList<>();
        this.url.setParams(params);
        assertEquals(params, this.url.getParams());
    }

    @Test
    public void testAddParam() {
        TypedParam param = new TypedParam("q", "string");
        this.url.addParam(param);
        assertTrue(this.url.getParams().contains(param));
    }

    @Test
    public void testGetRequiredParams() {
        var requiredParam = new TypedParam("username", "string", true);
        var optionalParam = new TypedParam("password", "string", false);

        this.url.addParam(requiredParam);
        this.url.addParam(optionalParam);

        assertTrue(this.url.getRequiredParams().contains(requiredParam));
        assertFalse(this.url.getRequiredParams().contains(optionalParam));
    }

    @Test
    public void testEqualsSelf() {
        assertTrue(this.url.equals(this.url));
    }

    @Test
    public void testEqualsDiffBaseUrl() {
        AbstractUrl other = new AbstractUrl("some_other_url");
        assertFalse(this.url.equals(other));
    }

    @Test
    public void testEqualsDiffRequiredParams() {
        AbstractUrl other = new AbstractUrl(this.url.getBaseUrl());
        other.addParam(new TypedParam("q", "string", true));
        assertFalse(this.url.equals(other));
    }

    @Test
    public void testEqualsDiffOptionalParams() {
        AbstractUrl other = new AbstractUrl(this.url.getBaseUrl());
        other.addParam(new TypedParam("q", "string"));
        assertTrue(this.url.equals(other));
    }

    @Test
    public void testEqualsIdentical() {
        AbstractUrl other = new AbstractUrl(this.url.getBaseUrl());
        assertTrue(this.url.equals(other));
    }
}
