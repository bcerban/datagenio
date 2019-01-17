package com.datagenio.databank;

import com.datagenio.databank.api.InputBuilder;
import com.datagenio.databank.api.InputPovider;
import com.datagenio.databank.util.XPathParser;
import org.jsoup.nodes.Element;

import java.util.HashMap;
import java.util.Map;

public class CompositeInputBuilder implements InputBuilder {

    public static String INPUT_SELECTOR = "input, textarea, select";

    private Map<String, InputPovider> providersByType;

    public CompositeInputBuilder() {
        this.providersByType = new HashMap<>();
        this.providersByType.put(DEFAULT, new RandomAlphabetic());
    }

    @Override
    public Map<String, String> buildInputs(Element element) {
        Map<String, String> inputs = new HashMap<>();

        if (this.isInput(element)) {
            inputs.put(XPathParser.getXPathFor(element), this.getInputForElement(element));
        } else {
            element.children().forEach((child) -> {
                if (this.isInput(child)) {
                    inputs.put(XPathParser.getXPathFor(child), this.getInputForElement(child));
                }
            });
        }
        return inputs;
    }

    private boolean isInput(Element element) {
        return element.is(INPUT_SELECTOR);
    }

    private String getInputForElement(Element element) {
        //TODO: provide input based on input type
        return this.providersByType.get(DEFAULT).provide();
    }
}
