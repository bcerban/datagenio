package com.datagenio.databank.provider;

import com.datagenio.databank.api.InputBuilder;
import com.datagenio.databank.api.InputPovider;
import com.github.javafaker.Faker;

import java.util.Map;

public class BooleanProvider implements InputPovider {
    private Faker faker;

    public BooleanProvider(Faker faker) {
        this.faker = faker;
    }

    @Override
    public String getType() {
        return InputBuilder.BOOLEAN;
    }

    @Override
    public String provide() {
        return faker.bool().bool() ? "true" : "false";
    }

    @Override
    public String provide(Map<String, Object> constraints) {
        return provide();
    }
}
