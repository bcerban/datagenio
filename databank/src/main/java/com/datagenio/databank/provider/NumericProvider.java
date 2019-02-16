package com.datagenio.databank.provider;

import com.datagenio.databank.api.InputBuilder;
import com.datagenio.databank.api.InputPovider;
import com.github.javafaker.Faker;
import org.apache.commons.lang.RandomStringUtils;

import java.util.Map;
import java.util.Random;

public class NumericProvider implements InputPovider {

    private Faker faker;

    public NumericProvider(Faker faker) {
        this.faker = faker;
    }

    @Override
    public String getType() {
        return InputBuilder.NUMBER;
    }

    @Override
    public String provide() {
        int length = new Random().nextInt((DEFAULT_MAX_LENGTH - DEFAULT_MIN_LENGTH) + 1) + DEFAULT_MIN_LENGTH;
        return RandomStringUtils.randomNumeric(length);
    }

    @Override
    public String provide(Map<String, Object> constraints) {
        if ((boolean)constraints.get(AS_STRING)) {
            return provideAsString(constraints);
        }

        return provideAsNumber(constraints);
    }

    private String provideAsNumber(Map<String, Object> constraints) {
        int minValue = constraints.containsKey(MIN_VALUE)
                ? (int) constraints.get(MIN_VALUE)
                : 0;

        int maxValue = constraints.containsKey(MAX_VALUE)
                ? (int) constraints.get(MAX_VALUE)
                : Integer.MAX_VALUE;

        return Integer.toString(faker.number().numberBetween(minValue, maxValue));
    }

    private String provideAsString(Map<String, Object> constraints) {
        int minLength = constraints.containsKey(MIN_LENGTH)
                ? (int) constraints.get(MIN_LENGTH)
                : DEFAULT_MIN_LENGTH;

        int maxLength = constraints.containsKey(MAX_LENGTH)
                ? (int) constraints.get(MAX_LENGTH)
                : DEFAULT_MAX_LENGTH;

        int length = new Random().nextInt((maxLength - minLength) + 1) + minLength;
        return RandomStringUtils.randomNumeric(length);
    }
}
