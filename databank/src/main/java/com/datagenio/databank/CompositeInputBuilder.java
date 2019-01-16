package com.datagenio.databank;

import com.datagenio.databank.api.InputBuilder;
import com.datagenio.databank.api.InputPovider;
import org.jsoup.nodes.Element;

import java.util.HashMap;
import java.util.Map;

public class CompositeInputBuilder implements InputBuilder {

    private Map<String, InputPovider> providersByType;

    public CompositeInputBuilder() {
        this.providersByType = new HashMap<>();
        this.providersByType.put(DEFAULT, new RandomAlphabetic());
    }

    @Override
    public Map<String, String> buildInputs(Element element) {
        Map<String, String> inputs = new HashMap<>();

        return inputs;
    }
}
