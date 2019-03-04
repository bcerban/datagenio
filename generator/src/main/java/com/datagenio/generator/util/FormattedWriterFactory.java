package com.datagenio.generator.util;

import com.datagenio.context.Context;
import com.datagenio.generator.api.FormattedWriter;

public class FormattedWriterFactory {

    public static final String FORMAT_CSV = "csv";
    public static final String FORMAT_ZAP = "zap";

    public static FormattedWriter get(Context context) {
        switch (context.getFormat()) {
            case FORMAT_CSV:
                return new CsvWriter(context);
            default:
                return new CsvWriter(context);
        }
    }
}
