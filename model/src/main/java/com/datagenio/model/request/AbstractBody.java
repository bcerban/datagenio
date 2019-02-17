package com.datagenio.model.request;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;
import java.util.stream.Collectors;

public class AbstractBody {
    public static final String MULTIPART_FORM_DATA = "multipart/form-data;";
    public static final String FORM_DATA_BOUNDARY  = "boundary=";
    public static final String FORM_DATA_CONTENT   = "Content-Disposition: form-data;";

    private Collection<TypedParam> typedParams;
    private String contentType;
    private String boundary;

    public AbstractBody() {
        this.typedParams = new ArrayList<>();
    }

    public Collection<TypedParam> getTypedParams() {
        return typedParams;
    }

    public void setTypedParams(Collection<TypedParam> typedParams) {
        this.typedParams = typedParams;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public String getBoundary() {
        return boundary;
    }

    public void setBoundary(String boundary) {
        this.boundary = boundary;
    }

    public void addParam(TypedParam param) {
        typedParams.add(param);
    }

    public Collection<TypedParam> getRequiredParams() {
        return this.typedParams.stream().filter(p -> p.isRequired()).collect(Collectors.toList());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AbstractBody that = (AbstractBody) o;
        return Objects.equals(getRequiredParams(), that.getRequiredParams());
    }

    @Override
    public int hashCode() {
        return Objects.hash(typedParams);
    }
}
