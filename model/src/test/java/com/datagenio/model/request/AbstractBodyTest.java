package com.datagenio.model.request;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.*;

public class AbstractBodyTest {

    public static final String ALPHANUMERIC = "alphanumeric";
    public static final String ALPHABETIC = "alphabetic";
    public static final String OBJECT = "object";

    private AbstractBody body;

    @Before
    public void setUp() {
        this.body = new AbstractBody();
    }

    @Test
    public void testGetProperties() {
        assertNotNull(this.body.getTypedParams());
    }

    @Test
    public void testAddProperty() {
        TypedParam property = new TypedParam("name", ALPHANUMERIC);
        this.body.addProperty(property);
        assertTrue(this.body.getTypedParams().contains(property));
    }

    @Test
    public void testSetProperties() {
        ArrayList<TypedParam> props = new ArrayList<>();
        this.body.setTypedParams(props);
        assertEquals(props, this.body.getTypedParams());
    }

    @Test
    public void testGetRequiredProperties() {
        var requiredProp = new TypedParam("username", ALPHANUMERIC, true);
        var optionalProp = new TypedParam("password", ALPHANUMERIC, false);

        this.body.addProperty(requiredProp);
        this.body.addProperty(optionalProp);

        assertTrue(this.body.getRequiredParams().contains(requiredProp));
        assertFalse(this.body.getRequiredParams().contains(optionalProp));
    }

    @Test
    public void testEqualsSelf() {
        assertTrue(this.body.equals(this.body));
    }

    @Test
    public void testEqualsDiffRequired() {
        AbstractBody other = new AbstractBody();
        other.addProperty(new TypedParam("person", ALPHANUMERIC, true));
        assertFalse(this.body.equals(other));
    }

    @Test
    public void testEqualsDiffOptional() {
        AbstractBody other = new AbstractBody();
        other.addProperty(new TypedParam("person", ALPHANUMERIC, false));
        assertTrue(this.body.equals(other));
    }

    @Test
    public void testEqualsIdentical() {
        AbstractBody other = new AbstractBody();
        assertTrue(this.body.equals(other));
    }
}
