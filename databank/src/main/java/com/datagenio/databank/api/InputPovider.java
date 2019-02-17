package com.datagenio.databank.api;

import java.util.Map;

public interface InputPovider {
    String MIN_LENGTH = "min_length";
    String MAX_LENGTH = "max_length";
    String MIN_VALUE = "min_value";
    String MAX_VALUE = "max_value";
    String REGEX = "regex";
    String AS_STRING = "as_string";
    int DEFAULT_MIN_LENGTH = 4;
    int DEFAULT_MAX_LENGTH = 256;

    String getType();
    String provide();
    String provide(Map<String, Object> constraints);
}
