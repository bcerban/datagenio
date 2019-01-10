package com.datagenio.model.request;

import com.datagenio.model.api.AbstractHttpBody;
import com.datagenio.model.api.TypedParam;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;
import java.util.stream.Collectors;

public class AbstractBody implements AbstractHttpBody {
    private Collection<TypedParam> properties;

    public AbstractBody() {
        this.properties = new ArrayList<>();
    }

    @Override
    public Collection<TypedParam> getProperties() {
        return properties;
    }

    @Override
    public void setProperties(Collection<TypedParam> properties) {
        this.properties = properties;
    }

    @Override
    public void addProperty(TypedParam param) {
        this.properties.add(param);
    }

    @Override
    public Collection<TypedParam> getRequiredProperties() {
        return this.properties.stream().filter(p -> p.isRequired()).collect(Collectors.toList());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AbstractBody that = (AbstractBody) o;
        return Objects.equals(this.getRequiredProperties(), that.getRequiredProperties());
    }

    @Override
    public int hashCode() {
        return Objects.hash(properties);
    }
}
