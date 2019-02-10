package com.datagenio.storage.translator;

import com.datagenio.model.WebTransitionImpl;
import com.datagenio.model.api.AbstractHttpRequest;
import com.datagenio.model.api.WebTransition;
import com.datagenio.storageapi.Properties;
import com.datagenio.storageapi.Translator;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class WebTransitionTranslator implements Translator<WebTransition, Map<String, Object>> {

    private Gson gson;

    public WebTransitionTranslator() {
        gson = new GsonBuilder().setPrettyPrinting().create();
    }

    @Override
    public Map<String, Object> buildProperties(WebTransition original) {
        Map<String, Object> properties = new HashMap<>();
        properties.put(Properties.ABSTRACT_REQUESTS, gson.toJson(original.getAbstractRequests()));
        return properties;
    }

    @Override
    public WebTransition translateFrom(Map<String, Object> translated) {
        WebTransition transition = new WebTransitionImpl();
        var abstractRequests = Arrays.asList(
                gson.fromJson((String)translated.get(Properties.ABSTRACT_REQUESTS), AbstractHttpRequest[].class)
        );
        transition.setRequests(abstractRequests);
        return transition;
    }
}
