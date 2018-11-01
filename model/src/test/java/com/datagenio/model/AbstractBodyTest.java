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
    public void testEqualsSelf() {
        assertTrue(this.body.equals(this.body));
    }

    @Test
    public void testEqualsDiff() {
        AbstractBody other = new AbstractBody();
        other.addPropery(new TypedParam("person", "object"));
        assertFalse(this.body.equals(other));
    }

    @Test
    public void testEqualsIdentical() {
        AbstractBody other = new AbstractBody();
        assertTrue(this.body.equals(other));
    }
}
