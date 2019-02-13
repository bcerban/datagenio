package com.datagenio.model.request;

import com.datagenio.model.ParamTypes;

import java.util.Objects;

public class TypedParam {

    private String name;
    private ParamTypes type;
    private boolean required = false;

    public TypedParam(String name, ParamTypes type) {
        this.name = name;
        this.type = type;
    }

    public TypedParam(String name, ParamTypes type, boolean required) {
        this.name = name;
        this.type = type;
        this.required = required;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ParamTypes getType() {
        return type;
    }

    public void setType(ParamTypes type) {
        this.type = type;
    }

    public boolean isRequired() {
        return required;
    }

    public void setIsRequired(boolean required) {
        this.required = required;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TypedParam that = (TypedParam) o;
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
