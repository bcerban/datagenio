package com.datagenio.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;
import java.util.stream.Collectors;

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

    public Collection<TypedParam> getRequiredParams() {
        return this.params.stream().filter(p -> p.isRequired()).collect(Collectors.toList());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AbstractUrl that = (AbstractUrl) o;
        return Objects.equals(baseUrl, that.baseUrl) &&
                Objects.equals(this.getRequiredParams(), that.getRequiredParams());
    }

    @Override
    public int hashCode() {
        return Objects.hash(baseUrl, params);
    }
}
