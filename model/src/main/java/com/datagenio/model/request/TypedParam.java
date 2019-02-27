package com.datagenio.model.request;

import java.util.Objects;

public class TypedParam {

    private String name;
    private String type;
    private String value;
    private boolean required = false;

    public TypedParam(String name, String type) {
        this.name = name;
        this.type = type;
    }

    public TypedParam(String name, String type, boolean required) {
        this.name = name;
        this.type = type;
        this.required = required;
    }

    public TypedParam(String name, String type, boolean required, String value) {
        this.name = name;
        this.type = type;
        this.value = value;
        this.required = required;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isRequired() {
        return required;
    }

    public void setIsRequired(boolean required) {
        this.required = required;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
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
        return String.format("%s:[%s] (%)", name, type, required ? "required" : "not required");
    }
}
