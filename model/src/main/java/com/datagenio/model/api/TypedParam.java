package com.datagenio.model.api;

public interface TypedParam {
    ParamTypes getType();
    String getName();
    boolean isRequired();

    void setType(ParamTypes type);
    void setName(String name);
    void setIsRequired(boolean required);
}
