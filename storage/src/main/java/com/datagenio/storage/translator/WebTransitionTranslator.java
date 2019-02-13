package com.datagenio.storage.translator;

import com.datagenio.model.WebTransition;
import com.datagenio.model.request.AbstractRequest;
import com.datagenio.storageapi.Properties;
import com.datagenio.storageapi.Translator;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
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
        var transition = new WebTransition();
        var abstractRequests = (ArrayList<AbstractRequest>)gson.fromJson(
                (String)translated.get(Properties.ABSTRACT_REQUESTS),
                new TypeToken<ArrayList<AbstractRequest>>() { }.getType()
        );
        transition.setRequests(abstractRequests);
        return transition;
    }
}
