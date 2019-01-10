package com.datagenio.model.request;

import com.datagenio.model.api.TypedParam;

import java.util.Objects;

public class TypedParamImpl implements TypedParam {

    private String name;
    private String type;
    private boolean required;

    public TypedParamImpl(String name, String type) {
        this.name = name;
        this.type = type;
        this.required = false;
    }

    public TypedParamImpl(String name, String type, boolean required) {
        this.name = name;
        this.type = type;
        this.required = required;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getType() {
        return type;
    }

    @Override
    public void setType(String type) {
        this.type = type;
    }

    @Override
    public boolean isRequired() {
        return required;
    }

    @Override
    public void setIsRequired(boolean required) {
        this.required = required;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TypedParamImpl that = (TypedParamImpl) o;
        return Objects.equals(name, that.name) &&
                Objects.equals(type, that.type) &&
                Objects.equals(required, that.required);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, type);
    }
}
