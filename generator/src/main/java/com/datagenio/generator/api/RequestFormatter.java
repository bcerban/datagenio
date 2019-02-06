package com.datagenio.generator.api;

import com.datagenio.model.api.AbstractHttpRequest;

import java.util.Map;

public interface RequestFormatter {
    String format(AbstractHttpRequest request, Map<String, String> inputs);
}
