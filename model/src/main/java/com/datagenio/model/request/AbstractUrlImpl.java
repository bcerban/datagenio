package com.datagenio.model.request;

import com.datagenio.model.api.AbstractUrl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;
import java.util.stream.Collectors;

public class AbstractUrlImpl implements AbstractUrl {
    private String baseUrl;
    private Collection<TypedParam> params;

    public AbstractUrlImpl() {
        this.params = new ArrayList<>();
    }

    public AbstractUrlImpl(String baseUrl) {
        this.baseUrl = baseUrl;
        this.params = new ArrayList<>();
    }

    @Override
    public String getBaseUrl() {
        return baseUrl;
    }

    @Override
    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    @Override
    public Collection<TypedParam> getTypedParams() {
        return params;
    }

    @Override
    public void setTypedParams(Collection<TypedParam> params) {
        this.params = params;
    }

    @Override
    public void addTypedParam(TypedParam param) {
        this.params.add(param);
    }

    @Override
    public void addTypedParam(String name, String type) {
        this.addTypedParam(new TypedParam(name, type, false));
    }

    @Override
    public void addTypedParam(String name, String type, boolean required) {
        this.params.add(new TypedParam(name, type, required));
    }

    @Override
    public Collection<TypedParam> getRequiredParams() {
        return this.params.stream().filter(p -> p.isRequired()).collect(Collectors.toList());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AbstractUrlImpl that = (AbstractUrlImpl) o;
        return Objects.equals(baseUrl, that.baseUrl) &&
                Objects.equals(this.getRequiredParams(), that.getRequiredParams());
    }

    @Override
    public int hashCode() {
        return Objects.hash(baseUrl, params);
    }
}
