package com.datagenio.databank.provider;

import com.datagenio.databank.api.InputPovider;
import com.github.javafaker.Faker;

import java.util.Map;

public class UsernameProvider implements InputPovider {
    private Faker faker;

    public UsernameProvider(Faker faker) {
        this.faker = faker;
    }

    @Override
    public String provide() {
        return faker.funnyName().name();
    }

    @Override
    public String provide(Map<String, Object> constraints) {
        return provide();
    }
}
