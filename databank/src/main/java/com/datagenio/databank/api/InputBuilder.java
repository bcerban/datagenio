package com.datagenio.databank.api;

import java.util.List;
import java.util.Map;

import com.datagenio.context.EventInput;
import com.datagenio.crawler.api.Eventable;
import com.datagenio.model.request.AbstractRequest;

public interface InputBuilder {
    String DEFAULT = "default";
    String ALPHABETIC = "alphabetic";
    String ALPHANUMERIC = "alphanumeric";
    String NUMBER = "number";
    String BOOLEAN = "boolean";
    String REGEX = "regex";
    String USERNAME = "username";
    String EMAIL = "email";
    String PASSWORD = "password";
    String DATE = "date";
    String TEXT = "text";
    String CHECKBOX = "checkbox";
    String RADIO = "radio";
    String HIDDEN = "hidden";
    String PARAGRAPH = "paragraph";

    InputPovider getProviderByType(String type);
    List<EventInput> buildInputs(Eventable event);
    Map<String, String> buildInputs(AbstractRequest request);
}
