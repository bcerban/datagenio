package com.datagenio.model.api;

import java.util.Collection;

public interface AbstractUrl {

    String getBaseUrl();
    Collection<TypedParam> getTypedParams();
    Collection<TypedParam> getRequiredParams();

    void setBaseUrl(String url);
    void setTypedParams(Collection<TypedParam> params);
    void addTypedParam(TypedParam param);
    void addTypedParam(String name, String type);
    void addTypedParam(String name, String type, boolean required);
}
