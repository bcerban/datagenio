package com.datagenio.model.request;

import com.datagenio.model.api.ParamTypes;
import com.datagenio.model.api.TypedParam;

import java.util.Objects;

public class TypedParamImpl implements TypedParam {

    private String name;
    private ParamTypes type;
    private boolean required = false;

    public TypedParamImpl(String name, ParamTypes type) {
        this.name = name;
        this.type = type;
    }

    public TypedParamImpl(String name, ParamTypes type, boolean required) {
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
    public ParamTypes getType() {
        return type;
    }

    @Override
    public void setType(ParamTypes type) {
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

    @Override
    public String toString() {
        return String.format("%s:[%s] (%)", name, type.toString(), required ? "required" : "not required");
    }
}
