package com.datagenio.model.request;


import com.datagenio.model.api.ParamTypes;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class TypedParamImplTest {

    private TypedParamImpl param;

    @Before
    public void setUp() {
        this.param = new TypedParamImpl("name", ParamTypes.ALPHANUMERIC);
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
        assertEquals(ParamTypes.ALPHANUMERIC, this.param.getType());
    }

    @Test
    public void testSetType() {
        this.param.setType(ParamTypes.ALPHANUMERIC);
        assertEquals(ParamTypes.ALPHANUMERIC, this.param.getType());
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
        TypedParamImpl other = new TypedParamImpl("name", ParamTypes.ALPHANUMERIC);
        assertTrue(this.param.equals(other));
    }

    @Test
    public void testEqualsDiffType() {
        TypedParamImpl other = new TypedParamImpl("name", ParamTypes.ALPHABETIC);
        assertFalse(this.param.equals(other));
    }

    @Test
    public void testEqualsDiffName() {
        TypedParamImpl other = new TypedParamImpl("person", ParamTypes.ALPHANUMERIC);
        assertFalse(this.param.equals(other));
    }
}
