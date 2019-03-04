package com.datagenio.databank;

import com.datagenio.context.Context;
import com.datagenio.context.EventInput;
import com.datagenio.crawler.api.Eventable;
import com.datagenio.databank.api.InputBuilder;
import com.datagenio.databank.api.InputPovider;
import com.datagenio.databank.provider.*;
import com.datagenio.databank.util.InputSelector;
import com.datagenio.databank.util.XPathParser;
import com.datagenio.model.request.AbstractRequest;
import com.github.javafaker.Faker;
import org.apache.commons.lang.StringUtils;
import org.jgrapht.alg.util.Pair;
import org.jsoup.nodes.Element;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CompositeInputBuilder implements InputBuilder {

    private Context context;
    private Map<String, InputPovider> providersByType;

    public CompositeInputBuilder(Context context) {
        this.context = context;

        var faker = new Faker();
        providersByType = new HashMap<>();
        providersByType.put(ALPHABETIC, new AlphabeticProvider());
        providersByType.put(ALPHANUMERIC, new AlphanumericProvider());
        providersByType.put(EMAIL, new EmailProvider(faker));
        providersByType.put(PASSWORD, new PasswordProvider(faker));
        providersByType.put(USERNAME, new UsernameProvider(faker));
        providersByType.put(NUMBER, new NumericProvider(faker));
        providersByType.put(REGEX, new RegexProvider(faker));
        providersByType.put(BOOLEAN, new BooleanProvider(faker));
        providersByType.put(DATE, new DateProvider(faker));
        providersByType.put(RADIO, new NumericProvider(faker));
        providersByType.put(PARAGRAPH, new ParagraphProvider(faker));
        providersByType.put(CHECKBOX, providersByType.get(BOOLEAN));
        providersByType.put(TEXT, providersByType.get(ALPHANUMERIC));
        providersByType.put(DEFAULT, providersByType.get(ALPHANUMERIC));
    }

    @Override
    public Map<String, String> buildInputs(AbstractRequest request) {
        var inputs = new HashMap<String, String>();

        request.getUrl().getTypedParams().forEach(p -> {
            inputs.put(p.getName(), getProviderByType(p.getType()).provide());
        });

        if (request.hasBody()) {
            request.getBody().getTypedParams().forEach(p -> {
                inputs.put(p.getName(), getProviderByType(p.getType()).provide());
            });
        }

        return inputs;
    }

    @Override
    public InputPovider getProviderByType(String type) {
        if (providersByType.containsKey(type)) {
            return providersByType.get(type);
        }

        return providersByType.get(DEFAULT);
    }

    @Override
    public List<EventInput> buildInputs(Eventable event) {
        return buildInputs(event, event.getSource(), new HashMap<>());
    }

    public List<EventInput> buildInputs(Eventable event, Element element, Map<String, String> presents) {
        List<EventInput> inputs = new ArrayList<>();

        if (isInput(element)) {
            String xpath = XPathParser.getXPathFor(element);
            inputs.add(getInputForElement(event.getId(), xpath, element, presents));
        } else {
            element.children().forEach((child) -> inputs.addAll(buildInputs(event, child, presents)));
        }
        return inputs;
    }

    public boolean isInput(Element element) {
        return InputSelector.isInput(element);
    }

    private EventInput getInputForElement(String eventId, String elementXpath, Element element, Map<String, String> presents) {
        EventInput definedInput = getInputDefinitionFromContext(eventId, elementXpath);
        if (definedInput != null) {
            if (!StringUtils.isNotBlank(definedInput.getInputType())) {
                if (!StringUtils.isNotBlank(definedInput.getInputValue())) {
                    definedInput.setInputValue(getInputForElement(element, definedInput.getInputType(), presents).getSecond());
                }
            } else if (StringUtils.isNotBlank(definedInput.getInputValue())) {
                definedInput.setInputType(REGEX);
            }

            return definedInput;
        }

        definedInput = new EventInput();
        definedInput.setEventId(eventId);
        definedInput.setXpath(elementXpath);

        if (InputSelector.isTextArea(element)) {
            var paragraphProvider = providersByType.get(PARAGRAPH);
            definedInput.setInputType(paragraphProvider.getType());
            definedInput.setInputValue(paragraphProvider.provide());
        } else {
            String elementType = element.attr("type");
            if (StringUtils.isNotBlank(elementType)) {
                var typeValuePair = getInputForElement(element, elementType, presents);
                definedInput.setInputType(typeValuePair.getFirst());
                definedInput.setInputValue(typeValuePair.getSecond());
            } else {
                var defaultProvider = providersByType.get(DEFAULT);
                definedInput.setInputType(defaultProvider.getType());
                definedInput.setInputValue(defaultProvider.provide());
            }
        }

        return definedInput;
    }

    private Pair<String, String> getInputForElement(Element element, String type, Map<String, String> presents) {
        if (type.equals(HIDDEN)) return new Pair<>(HIDDEN, element.val());

        String maxLength = element.attr(InputPovider.MAX_LENGTH);
        String pattern = element.attr("pattern");
        String minValue = element.attr(InputPovider.MIN_VALUE);
        String maxValue = element.attr(InputPovider.MAX_VALUE);

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
            return new Pair<>(PASSWORD, presents.get(PASSWORD));
        }

        var provider = getProviderByType(type);
        return new Pair<>(provider.getType(), provider.provide(constraints));
    }

    private EventInput getInputDefinitionFromContext(String eventId, String elementXpath) {
        var maybe = context.getEventInputs()
                .stream()
                .filter(i -> i.getEventId().equals(eventId) && i.getXpath().equals(elementXpath))
                .findFirst();

        if (maybe.isPresent()) {
            var defined = maybe.get();
            if (canUseDefined(defined)) return defined;
        }
        return null;
    }

    private boolean canUseDefined(EventInput defined) {
        return StringUtils.isNotBlank(defined.getInputType()) || StringUtils.isNotBlank(defined.getInputValue());
    }
}
