package com.datagenio.model.request;

import com.datagenio.model.api.AbstractUrl;
import com.datagenio.model.api.ParamTypes;
import com.datagenio.model.api.TypedParam;
import org.apache.commons.lang.StringUtils;

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
    public void addTypedParam(String name, ParamTypes type) {
        this.addTypedParam(new TypedParamImpl(name, type, false));
    }

    @Override
    public void addTypedParam(String name, ParamTypes type, boolean required) {
        this.params.add(new TypedParamImpl(name, type, required));
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

    @Override
    public String toString() {
        String ps = params.stream().map(p -> "{" + p.getName() + "}").collect(Collectors.joining("&"));
        return params.isEmpty() ? baseUrl : String.format("%s?%s", baseUrl, ps);
    }
}
