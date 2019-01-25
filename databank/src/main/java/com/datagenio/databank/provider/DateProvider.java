package com.datagenio.databank.provider;

import com.datagenio.databank.api.InputPovider;
import com.github.javafaker.Faker;

import java.util.Map;

public class DateProvider implements InputPovider {
    private Faker faker = new Faker();

    @Override
    public String provide() {
        return faker.date().birthday().toString();
    }

    @Override
    public String provide(Map<String, Object> constraints) {
        return provide();
    }
}
