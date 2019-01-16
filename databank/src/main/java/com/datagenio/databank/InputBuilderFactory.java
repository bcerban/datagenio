package com.datagenio.databank;

import com.datagenio.databank.api.InputBuilder;

public class InputBuilderFactory {

    public static InputBuilder get() {
        return new CompositeInputBuilder();
    }
}
