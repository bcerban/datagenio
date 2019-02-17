package com.datagenio.generator.converter;

import com.datagenio.context.EventInput;
import com.datagenio.crawler.api.RemoteRequestBodyPart;
import com.datagenio.databank.api.InputBuilder;
import com.datagenio.databank.util.XPathParser;
import com.datagenio.model.request.TypedParam;
import org.jsoup.nodes.Element;

import java.util.List;

public class ParamProcessor {

    public static final String ATTR_NAME     = "name";
    public static final String ATTR_REQUIRED = "required";

    /**
     * Converts a {@link RemoteRequestBodyPart} into a {@link TypedParam}, by matching all inputs passed on
     * event execution to parameter by name.
     *
     * @param part the {@link RemoteRequestBodyPart} to convert
     * @param source the source {@link Element} on which the event was executed
     * @param inputs the inputs passed on event execution
     * @param inputBuilder the {@link InputBuilder} that defines input types
     * @return a new {@link TypedParam}
     */
    public static TypedParam processPart(RemoteRequestBodyPart part, Element source, List<EventInput> inputs, InputBuilder inputBuilder) {
        TypedParam param = new TypedParam(part.getName(), "");
        Element inputElement = findInputElementByName(part.getName(), source);

        if (inputElement != null) {
            var xpath = XPathParser.getXPathFor(inputElement);
            var input = inputs.stream().filter(i -> i.getXpath().equals(xpath)).findFirst();

            if (input.isPresent()) {
                param.setType(input.get().getInputType());
            } else {
                param.setType(inputBuilder.getProviderByType(part.getContentType()).getType());
            }

            if (inputElement.hasAttr(ATTR_REQUIRED)) param.setIsRequired(true);
        } else {
            param.setType(inputBuilder.getProviderByType(part.getContentType()).getType());
        }

        return param;
    }

    /**
     * Creates a {@link TypedParam}, by name, matching all inputs passed on
     * event execution to parameter by name.
     *
     * @param paramName param name
     * @param source the source {@link Element} on which the event was executed
     * @param inputs the inputs passed on event execution
     * @param inputBuilder the {@link InputBuilder} that defines input types
     * @return a new {@link TypedParam}
     */
    public static TypedParam processPart(String paramName, Element source, List<EventInput> inputs, InputBuilder inputBuilder) {
        TypedParam param = new TypedParam(paramName, "");
        Element inputElement = findInputElementByName(paramName, source);

        if (inputElement != null) {
            var xpath = XPathParser.getXPathFor(inputElement);
            var input = inputs.stream().filter(i -> i.getXpath().equals(xpath)).findFirst();

            if (input.isPresent()) {
                param.setType(input.get().getInputType());
            } else {
                param.setType(inputBuilder.getProviderByType(InputBuilder.DEFAULT).getType());
            }

            if (inputElement.hasAttr(ATTR_REQUIRED)) param.setIsRequired(true);
        } else {
            param.setType(inputBuilder.getProviderByType(InputBuilder.DEFAULT).getType());
        }

        return param;
    }

    /**
     * Finds an input element by name, which can be the source itself, or its first
     * child element with the name {@code name}.
     *
     * @param name the name of the desired element
     * @param source the Element in which to search
     * @return Element if found, or <b>{@code null}</b> otherwise
     */
    private static Element findInputElementByName(String name, Element source) {
        Element inputElement;
        if (source.attr(ATTR_NAME).equals(name)) {
            inputElement = source;
        } else {
            String selector = String.format("[%s=\"%s\"]", ATTR_NAME, name);
            inputElement = source.selectFirst(selector);
        }
        return inputElement;
    }
}
