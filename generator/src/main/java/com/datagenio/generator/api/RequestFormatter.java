package com.datagenio.generator.api;

import com.datagenio.model.request.AbstractRequest;

import java.util.Map;

public interface RequestFormatter {
    String format(AbstractRequest request, Map<String, String> inputs);
    String getFormatExtension();
    String getHeaderLine();
    boolean requiredHeader();
}
