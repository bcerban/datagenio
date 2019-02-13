package com.datagenio.databank.api;

import java.util.Map;

import com.datagenio.model.request.AbstractRequest;
import org.jsoup.nodes.Element;

public interface InputBuilder {
    String DEFAULT = "default";
    String ALPHABETIC = "alphabetic";
    String ALPHANUMERIC = "alphanumeric";
    String NUMBER = "number";
    String BOOLEAN = "boolean";
    String REGEX = "regex";
    String EMAIL = "email";
    String PASSWORD = "password";
    String DATE = "date";
    String TEXT = "text";
    String CHECKBOX = "checkbox";
    String RADIO = "radio";
    String HIDDEN = "hidden";

    Map<String, String> buildInputs(Element element);
    Map<String, String> buildInputs(AbstractRequest request);
}
