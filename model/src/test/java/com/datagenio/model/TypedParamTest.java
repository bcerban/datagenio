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
}
