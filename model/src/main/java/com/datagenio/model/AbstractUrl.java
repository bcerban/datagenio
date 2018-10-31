package com.datagenio.model;

import java.util.ArrayList;
import java.util.Collection;

public class AbstractUrl {
    private String baseUrl;
    private Collection<TypedParam> params;

    public AbstractUrl() {
        this.params = new ArrayList<>();
    }

    public AbstractUrl(String baseUrl) {
        this.baseUrl = baseUrl;
        this.params = new ArrayList<>();
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public Collection<TypedParam> getParams() {
        return params;
    }

    public void setParams(Collection<TypedParam> params) {
        this.params = params;
    }

    public void addParam(TypedParam param) {
        this.params.add(param);
    }
}
