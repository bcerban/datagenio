package com.datagenio.databank;

import com.datagenio.context.Context;
import com.datagenio.databank.api.InputBuilder;

public class InputBuilderFactory {

    private static InputBuilder compositeBuilder;

    public static InputBuilder get(Context context) {
        if (compositeBuilder == null) {
            compositeBuilder = new CompositeInputBuilder(context);
        }

        return compositeBuilder;
    }

    public static InputBuilder create(Context context) {
        return new CompositeInputBuilder(context);
    }
}
