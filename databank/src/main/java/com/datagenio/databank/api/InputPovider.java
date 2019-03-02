package com.datagenio.databank.api;

import java.util.Map;

public interface InputPovider {
    String MIN_LENGTH = "minlength";
    String MAX_LENGTH = "maxlength";
    String MIN_VALUE = "min";
    String MAX_VALUE = "max";
    String REGEX = "regex";
    String AS_STRING = "as_string";
    int DEFAULT_MIN_LENGTH = 4;
    int DEFAULT_MAX_LENGTH = 100;
    int DEFAULT_MIN_LENGTH_NUMERIC = 1;
    int DEFAULT_MAX_LENGTH_NUMERIC = 4;
    int DEFAULT_MIN_VALUE = 1;
    int DEFAULT_MAX_VALUE = 9999;

    String getType();
    String provide();
    String provide(Map<String, Object> constraints);
}
