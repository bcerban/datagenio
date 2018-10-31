package com.datagenio.model;

import java.util.ArrayList;
import java.util.Collection;

public class AbstractBody {
    private Collection<TypedParam> properties;

    public AbstractBody() {
        this.properties = new ArrayList<TypedParam>();
    }

    public Collection<TypedParam> getProperties() {
        return properties;
    }

    public void setProperties(Collection<TypedParam> properties) {
        this.properties = properties;
    }

    public void addPropery(TypedParam param) {
        this.properties.add(param);
    }
}
