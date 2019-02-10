package com.datagenio.storageapi;

import java.util.Map;

public interface Translator<T, U> {

    String BOOLEAN_TRUE  = "true";
    String BOOLEAN_FALSE = "false";

    Map<String, Object> buildProperties(T original);
    T translateFrom(U translated);
}
