package com.datagenio.databank.provider;

import com.datagenio.databank.api.InputPovider;
import org.apache.commons.lang.RandomStringUtils;

import java.util.Map;
import java.util.Random;

public class AlphanumericProvider implements InputPovider {

    @Override
    public String provide() {
        int length = new Random().nextInt((DEFAULT_MAX_LENGTH - DEFAULT_MIN_LENGTH) + 1) + DEFAULT_MIN_LENGTH;
        return RandomStringUtils.randomAlphanumeric(length);
    }

    @Override
    public String provide(Map<String, Object> constraints) {
        int minLength = constraints.containsKey(MIN_LENGTH)
                ? (int) constraints.get(MIN_LENGTH)
                : DEFAULT_MIN_LENGTH;

        int maxLength = constraints.containsKey(MAX_LENGTH)
                ? (int) constraints.get(MAX_LENGTH)
                : DEFAULT_MAX_LENGTH;

        int length = new Random().nextInt((maxLength - minLength) + 1) + minLength;
        return RandomStringUtils.randomAlphanumeric(length);
    }
}
