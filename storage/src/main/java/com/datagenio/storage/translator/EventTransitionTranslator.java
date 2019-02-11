package com.datagenio.storage.translator;

import com.datagenio.crawler.api.RemoteRequest;
import com.datagenio.crawler.api.Transitionable;
import com.datagenio.crawler.model.Transition;
import com.datagenio.storageapi.Properties;
import com.datagenio.storageapi.Translator;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class EventTransitionTranslator implements Translator<Transitionable, Map<String, Object>> {

    private Gson gson;

    public EventTransitionTranslator() {
        gson = new GsonBuilder().setPrettyPrinting().create();
    }

    @Override
    public Map<String, Object> buildProperties(Transitionable original) {
        Map<String, Object> properties = new HashMap<>();
        properties.put(Properties.EXECUTED_EVENT_ID, original.getExecutedEvent().getEvent().getEventIdentifier());
        properties.put(Properties.STATUS, original.getStatus().toString());
        properties.put(Properties.DATA_INPUTS, gson.toJson(original.getExecutedEvent().getDataInputs()));
        properties.put(Properties.CONCRETE_REQUESTS, gson.toJson(original.getRequests()));
        return properties;
    }

    @Override
    public Transitionable translateFrom(Map<String, Object> translated) {
        Transitionable transition = new Transition();

        if (translated.containsKey(Properties.CONCRETE_REQUESTS)) {
            var requests = Arrays.asList(
                    gson.fromJson((String)translated.get(Properties.CONCRETE_REQUESTS), RemoteRequest[].class)
            );
            transition.setRequests(requests);
        }

        return transition;
    }
}
