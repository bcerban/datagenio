package com.datagenio.generator.util;

import com.datagenio.generator.api.RequestFormatter;
import com.datagenio.model.request.AbstractRequest;

import java.util.Map;

public class CsvFormatter implements RequestFormatter {

    @Override
    public String format(AbstractRequest request, Map<String, String> inputs) {
        return "";
    }
}
