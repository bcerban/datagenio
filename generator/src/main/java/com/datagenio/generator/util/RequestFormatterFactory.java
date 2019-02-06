package com.datagenio.generator.util;

import com.datagenio.generator.api.RequestFormatter;

public class RequestFormatterFactory {

    public static RequestFormatter get() {
        //TODO: read from configuration
        return new CsvFormatter();
    }
}
