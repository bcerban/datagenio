package com.datagenio.databank.provider;

import com.datagenio.databank.api.InputBuilder;
import com.datagenio.databank.api.InputPovider;
import com.github.javafaker.Faker;

import java.util.Map;

public class ParagraphProvider implements InputPovider {

    private Faker faker;

    public ParagraphProvider(Faker faker) {
        this.faker = faker;
    }

    @Override
    public String getType() {
        return InputBuilder.PARAGRAPH;
    }

    @Override
    public String provide() {
        return faker.lorem().sentence();
    }

    @Override
    public String provide(Map<String, Object> constraints) {
        return faker.lorem().sentence();
    }
}
