package com.datagenio.model.request;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;
import java.util.stream.Collectors;

public class AbstractUrl {
    private String baseUrl;
    private Collection<TypedParam> typedParams;

    public AbstractUrl(String baseUrl) {
        this.baseUrl = baseUrl;
        this.typedParams = new ArrayList<>();
    }

    public AbstractUrl(String baseUrl, Collection<TypedParam> typedParams) {
        this.baseUrl = baseUrl;
        this.typedParams = typedParams;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public Collection<TypedParam> getTypedParams() {
        return typedParams;
    }

    public void setTypedParams(Collection<TypedParam> params) {
        this.typedParams = params;
    }

    public void addTypedParam(TypedParam param) {
        this.typedParams.add(param);
    }

    public void addTypedParam(String name, String type) {
        this.addTypedParam(new TypedParam(name, type, false));
    }

    public void addTypedParam(String name, String type, boolean required) {
        this.typedParams.add(new TypedParam(name, type, required));
    }

    public Collection<TypedParam> getRequiredParams() {
        return this.typedParams.stream().filter(p -> p.isRequired()).collect(Collectors.toList());
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
        return Objects.hash(baseUrl, typedParams);
    }

    @Override
    public String toString() {
        String ps = typedParams.stream().map(p -> "{" + p.getName() + "}").collect(Collectors.joining("&"));
        return typedParams.isEmpty() ? baseUrl : String.format("%s?%s", baseUrl, ps);
    }
}
