package com.datagenio.model.api;

import java.util.Collection;

public interface AbstractHttpBody {
    Collection<TypedParam> getProperties();
    Collection<TypedParam> getRequiredProperties();

    void setProperties(Collection<TypedParam> properties);
    void addProperty(TypedParam property);
}
