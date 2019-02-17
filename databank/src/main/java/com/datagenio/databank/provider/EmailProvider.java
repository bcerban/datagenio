package com.datagenio.databank.provider;

import com.datagenio.databank.api.InputBuilder;
import com.datagenio.databank.api.InputPovider;
import com.github.javafaker.Faker;

import java.util.Map;

public class EmailProvider implements InputPovider {
    private Faker faker;

    public EmailProvider(Faker faker) {
        this.faker = faker;
    }

    @Override
    public String getType() {
        return InputBuilder.EMAIL;
    }

    @Override
    public String provide() {
        return faker.internet().emailAddress();
    }

    @Override
    public String provide(Map<String, Object> constraints) {
        return provide();
    }
}
