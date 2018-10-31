package com.datagenio.model;

public class TypedParam {

    private String name;
    private String type;

    public TypedParam() { }

    public TypedParam(String name, String type) {
        this.name = name;
        this.type = type;
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




}
