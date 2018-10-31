package com.datagenio.model;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

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
}
