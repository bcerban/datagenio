package com.datagenio.model.api;

public interface TypedParam {
    String getType();
    String getName();
    boolean isRequired();

    void setType(String type);
    void setName(String name);
    void setIsRequired(boolean required);
}
