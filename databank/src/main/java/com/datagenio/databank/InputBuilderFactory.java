package com.datagenio.databank;

import com.datagenio.context.Context;
import com.datagenio.databank.api.InputBuilder;

public class InputBuilderFactory {

    public static InputBuilder get(Context context) {
        return new CompositeInputBuilder(context);
    }
}
