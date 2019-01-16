package com.datagenio.databank;

import java.util.Random;
import com.datagenio.databank.api.InputPovider;
import org.apache.commons.lang.RandomStringUtils;

public class RandomAlphabetic implements InputPovider {

    public static int MIN_LENGTH = 4;
    public static int MAX_LENGTH = 256;

    @Override
    public String provide() {
        int length = new Random().nextInt((MAX_LENGTH - MIN_LENGTH) + 1) + MIN_LENGTH;
        return RandomStringUtils.randomAlphabetic(length);
    }
}
