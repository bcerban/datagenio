package com.datagenio.model;


import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class TypedParamTest {

    private TypedParam param;

    @Before
    public void setUp() {
        this.param = new TypedParam("name", "string");
    }

    @Test
    public void testGetName() {
        assertEquals("name", this.param.getName());
    }

    @Test
    public void testSetName() {
        this.param.setName("q");
        assertEquals("q", this.param.getName());
    }

    @Test
    public void testGetType() {
        assertEquals("string", this.param.getType());
    }

    @Test
    public void testSetType() {
        this.param.setType("alphanumeric");
        assertEquals("alphanumeric", this.param.getType());
    }

    @Test
    public void testEqualsSelf() {
        assertTrue(this.param.equals(this.param));
    }

    @Test
    public void testEqualsIdentical() {
        TypedParam other = new TypedParam("name", "string");
        assertTrue(this.param.equals(other));
    }

    @Test
    public void testEqualsDiffType() {
        TypedParam other = new TypedParam("name", "object");
        assertFalse(this.param.equals(other));
    }

    @Test
    public void testEqualsDiffName() {
        TypedParam other = new TypedParam("person", "string");
        assertFalse(this.param.equals(other));
    }
}
