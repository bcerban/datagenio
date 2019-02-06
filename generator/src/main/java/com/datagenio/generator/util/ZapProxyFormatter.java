package com.datagenio.generator.util;

import com.datagenio.generator.api.RequestFormatter;
import com.datagenio.model.api.AbstractHttpRequest;

import java.util.Map;

public class ZapProxyFormatter implements RequestFormatter {

    @Override
    public String format(AbstractHttpRequest request, Map<String, String> inputs) {
        return "";
    }
}
