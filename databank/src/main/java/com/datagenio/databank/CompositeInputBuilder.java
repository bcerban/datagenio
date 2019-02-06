package com.datagenio.databank;

import com.datagenio.databank.api.InputBuilder;
import com.datagenio.databank.api.InputPovider;
import com.datagenio.databank.provider.*;
import com.datagenio.databank.util.XPathParser;
import com.datagenio.model.api.AbstractHttpRequest;
import org.apache.commons.lang.StringUtils;
import org.jsoup.nodes.Element;

import java.util.HashMap;
import java.util.Map;

public class CompositeInputBuilder implements InputBuilder {

    public static String INPUT_SELECTOR = "input, textarea, select";

    private Map<String, InputPovider> providersByType;

    public CompositeInputBuilder() {
        providersByType = new HashMap<>();
        providersByType.put(ALPHABETIC, new AlphabeticProvider());
        providersByType.put(ALPHANUMERIC, new AlphanumericProvider());
        providersByType.put(EMAIL, new EmailProvider());
        providersByType.put(PASSWORD, new PasswordProvider());
        providersByType.put(NUMBER, new NumericProvider());
        providersByType.put(REGEX, new RegexProvider());
        providersByType.put(BOOLEAN, new BooleanProvider());
        providersByType.put(DATE, new DateProvider());
        providersByType.put(RADIO, new NumericProvider());
        providersByType.put(CHECKBOX, new BooleanProvider());
        providersByType.put(TEXT, providersByType.get(ALPHANUMERIC));
        providersByType.put(DEFAULT, providersByType.get(ALPHANUMERIC));
    }

    @Override
    public Map<String, String> buildInputs(AbstractHttpRequest request) {
        return new HashMap<>();
    }

    @Override
    public Map<String, String> buildInputs(Element element) {
        return buildInputs(element, new HashMap<>());
    }

    public Map<String, String> buildInputs(Element element, Map<String, String> presents) {
        Map<String, String> inputs = new HashMap<>();

        if (isInput(element)) {
            inputs.put(XPathParser.getXPathFor(element), getInputForElement(element, presents));
        } else {
            element.children().forEach((child) -> inputs.putAll(buildInputs(child, presents)));
        }
        return inputs;
    }

    private boolean isInput(Element element) {
        String disabled = element.attr("disabled");
        String readOnly = element.attr("readonly");
        if (StringUtils.isNotBlank(disabled) && disabled.equals("true")) return false;
        if (StringUtils.isNotBlank(readOnly) && disabled.equals("true")) return false;

        return element.is(INPUT_SELECTOR);
    }

    private String getInputForElement(Element element, Map<String, String> presents) {
        String elementType = element.attr("type");
        if (StringUtils.isNotBlank(elementType)) {
            return getInputForElement(element, elementType, presents);
        }

        return providersByType.get(DEFAULT).provide();
    }

    private String getInputForElement(Element element, String type, Map<String, String> presents) {
        if (type.equals(HIDDEN)) return element.val();

        String maxLength = element.attr("maxlength");
        String pattern = element.attr("pattern");
        String minValue = element.attr("min");
        String maxValue = element.attr("max");

        Map<String, Object> constraints = new HashMap<>();
        constraints.put(InputPovider.AS_STRING, true);
        constraints.put(InputPovider.REGEX, pattern);

        if (StringUtils.isNotBlank(pattern)) type = REGEX;
        if (StringUtils.isNotBlank(maxLength)) {
            try {
                constraints.put(InputPovider.MAX_LENGTH, Integer.parseInt(maxLength));
            } catch (NumberFormatException e) { }
        }

        if (StringUtils.isNotBlank(minValue) && StringUtils.isNotBlank(maxValue)) {
            try {
                constraints.put(InputPovider.MIN_VALUE, Integer.parseInt(minValue));
                constraints.put(InputPovider.MAX_VALUE, Integer.parseInt(maxValue));
                constraints.put(InputPovider.AS_STRING, false);
            } catch (NumberFormatException e) { }
        }

        if (type.equals(PASSWORD)) {
            if (!presents.containsKey(PASSWORD)) {
                presents.put(PASSWORD, providersByType.get(PASSWORD).provide(constraints));
            }
            return presents.get(PASSWORD);
        }

        if (providersByType.containsKey(type)) {
            return providersByType.get(type).provide(constraints);
        }

        return providersByType.get(DEFAULT).provide(constraints);
    }
}
