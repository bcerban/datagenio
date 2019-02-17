package com.datagenio.model.request;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class TypedParamTest {

    private TypedParam param;

    @Before
    public void setUp() {
        this.param = new TypedParam("name", AbstractBodyTest.ALPHANUMERIC);
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
        assertEquals(AbstractBodyTest.ALPHANUMERIC, this.param.getType());
    }

    @Test
    public void testSetType() {
        this.param.setType(AbstractBodyTest.ALPHANUMERIC);
        assertEquals(AbstractBodyTest.ALPHANUMERIC, this.param.getType());
    }

    @Test
    public void testIsRequired() {
        assertFalse(this.param.isRequired());
    }

    @Test
    public void testSetRequired() {
        this.param.setIsRequired(true);
        assertTrue(this.param.isRequired());
    }

    @Test
    public void testEqualsSelf() {
        assertTrue(this.param.equals(this.param));
    }

    @Test
    public void testEqualsIdentical() {
        TypedParam other = new TypedParam("name", AbstractBodyTest.ALPHANUMERIC);
        assertTrue(this.param.equals(other));
    }

    @Test
    public void testEqualsDiffType() {
        TypedParam other = new TypedParam("name", AbstractBodyTest.ALPHABETIC);
        assertFalse(this.param.equals(other));
    }

    @Test
    public void testEqualsDiffName() {
        TypedParam other = new TypedParam("person", AbstractBodyTest.ALPHANUMERIC);
        assertFalse(this.param.equals(other));
    }
}
