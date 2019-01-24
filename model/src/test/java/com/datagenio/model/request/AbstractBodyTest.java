package com.datagenio.model.request;

import com.datagenio.model.api.ParamTypes;
import com.datagenio.model.api.TypedParam;
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
        TypedParamImpl property = new TypedParamImpl("name", ParamTypes.ALPHANUMERIC);
        this.body.addProperty(property);
        assertTrue(this.body.getProperties().contains(property));
    }

    @Test
    public void testSetProperties() {
        ArrayList<TypedParam> props = new ArrayList<>();
        this.body.setProperties(props);
        assertEquals(props, this.body.getProperties());
    }

    @Test
    public void testGetRequiredProperties() {
        var requiredProp = new TypedParamImpl("username", ParamTypes.ALPHANUMERIC, true);
        var optionalProp = new TypedParamImpl("password", ParamTypes.ALPHANUMERIC, false);

        this.body.addProperty(requiredProp);
        this.body.addProperty(optionalProp);

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
        other.addProperty(new TypedParamImpl("person", ParamTypes.ALPHANUMERIC, true));
        assertFalse(this.body.equals(other));
    }

    @Test
    public void testEqualsDiffOptional() {
        AbstractBody other = new AbstractBody();
        other.addProperty(new TypedParamImpl("person", ParamTypes.ALPHANUMERIC, false));
        assertTrue(this.body.equals(other));
    }

    @Test
    public void testEqualsIdentical() {
        AbstractBody other = new AbstractBody();
        assertTrue(this.body.equals(other));
    }
}
