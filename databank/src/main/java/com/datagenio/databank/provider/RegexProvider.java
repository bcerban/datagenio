package com.datagenio.databank.provider;

import com.datagenio.databank.api.InputPovider;
import com.github.javafaker.Faker;

import java.util.Map;

public class RegexProvider implements InputPovider {

    private Faker faker;

    public RegexProvider(Faker faker) {
        this.faker = faker;
    }

    @Override
    public String provide() {
        return "";
    }

    @Override
    public String provide(Map<String, Object> constraints) {
        if (constraints.containsKey(REGEX)) {
            return faker.regexify((String) constraints.get(REGEX));
        }

        return provide();
    }
}
