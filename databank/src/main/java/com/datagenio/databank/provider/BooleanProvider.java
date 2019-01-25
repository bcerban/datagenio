package com.datagenio.databank.provider;

import com.datagenio.databank.api.InputPovider;
import com.github.javafaker.Faker;

import java.util.Map;

public class BooleanProvider implements InputPovider {
    private Faker faker = new Faker();

    @Override
    public String provide() {
        return faker.bool().bool() ? "true" : "false";
    }

    @Override
    public String provide(Map<String, Object> constraints) {
        return provide();
    }
}
