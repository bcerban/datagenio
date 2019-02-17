package com.datagenio.generator.util;

import com.datagenio.generator.api.RequestFormatter;
import com.datagenio.model.request.AbstractRequest;

import java.util.Map;

public class ZapProxyFormatter implements RequestFormatter {

    public static final String ZAP_EXTENSION = "txt";

    @Override
    public String format(AbstractRequest request, Map<String, String> inputs) {
        return "";
    }

    @Override
    public String getFormatExtension() {
        return ZAP_EXTENSION;
    }

    @Override
    public String getHeaderLine() {
        return "";
    }

    @Override
    public boolean requiredHeader() {
        return false;
    }
}
