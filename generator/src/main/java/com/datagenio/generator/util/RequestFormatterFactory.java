package com.datagenio.generator.util;

import com.datagenio.generator.api.RequestFormatter;

public class RequestFormatterFactory {

    public static final String FORMAT_CSV = "csv";
    public static final String FORMAT_ZAP = "zap";

    public static RequestFormatter get(String format) {
        switch (format) {
            case FORMAT_CSV:
                return new CsvFormatter();
            case FORMAT_ZAP:
                return new ZapProxyFormatter();
            default:
                return new CsvFormatter();
        }
    }
}
