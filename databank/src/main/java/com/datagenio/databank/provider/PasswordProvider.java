package com.datagenio.databank.provider;

import com.datagenio.databank.api.InputBuilder;
import com.datagenio.databank.api.InputPovider;
import com.github.javafaker.Faker;

import java.util.Map;

public class PasswordProvider implements InputPovider {
    private Faker faker;

    public PasswordProvider(Faker faker) {
        this.faker = faker;
    }

    @Override
    public String getType() {
        return InputBuilder.PASSWORD;
    }

    @Override
    public String provide() {
        return faker.internet().password(8, 16, true, true);
    }

    @Override
    public String provide(Map<String, Object> constraints) {
        if (constraints.containsKey(MIN_LENGTH) && constraints.containsKey(MAX_LENGTH)) {
            return faker.internet().password((int)constraints.get(MIN_LENGTH), (int)constraints.get(MAX_LENGTH));
        }

        return provide();
    }
}
