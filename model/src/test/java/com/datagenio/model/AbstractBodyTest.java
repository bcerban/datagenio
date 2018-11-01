package com.datagenio.model;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.*;

public class AbstractBodyTest {

    private AbstractBody body;

    @Before
    public void setUp() {
        this.body = new AbstractBody();
    }

    @Test
    public void testGetProperties() {
        assertNotNull(this.body.getProperties());
    }

    @Test
    public void testAddProperty() {
        TypedParam property = new TypedParam("name", "string");
        this.body.addPropery(property);
        assertTrue(this.body.getProperties().contains(property));
    }

    @Test
    public void testSetProperties() {
        ArrayList<TypedParam> props = new ArrayList<TypedParam>();
        this.body.setProperties(props);
        assertEquals(props, this.body.getProperties());
    }

    @Test
    public void testGetRequiredProperties() {
        var requiredProp = new TypedParam("username", "string", true);
        var optionalProp = new TypedParam("password", "string", false);

        this.body.addPropery(requiredProp);
        this.body.addPropery(optionalProp);

        assertTrue(this.body.getRequiredProperties().contains(requiredProp));
        assertFalse(this.body.getRequiredProperties().contains(optionalProp));
    }

    @Test
    public void testEqualsSelf() {
        assertTrue(this.body.equals(this.body));
    }

    @Test
    public void testEqualsDiffRequired() {
        AbstractBody other = new AbstractBody();
        other.addPropery(new TypedParam("person", "object", true));
        assertFalse(this.body.equals(other));
    }

    @Test
    public void testEqualsDiffOptional() {
        AbstractBody other = new AbstractBody();
        other.addPropery(new TypedParam("person", "object", false));
        assertTrue(this.body.equals(other));
    }

    @Test
    public void testEqualsIdentical() {
        AbstractBody other = new AbstractBody();
        assertTrue(this.body.equals(other));
    }
}
