package com.datagenio.storage.translator;

import com.datagenio.crawler.api.Eventable;
import com.datagenio.crawler.model.ExecutableEvent;
import com.datagenio.storageapi.Properties;
import com.datagenio.storageapi.Translator;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.util.HashMap;
import java.util.Map;

public class EventTranslator implements Translator<Eventable, Map<String, Object>> {

    private Gson gson;

    public EventTranslator() {
        gson = new GsonBuilder().setPrettyPrinting().create();
    }

    @Override
    public Map<String, Object> buildProperties(Eventable original) {
        Map<String, Object> properties = new HashMap<>();
        properties.put(Properties.IDENTIFICATION, original.getId());
        properties.put(Properties.EVENT_IDENTIFICATION, original.getEventIdentifier());
        properties.put(Properties.XPATH, original.getXpath());
        properties.put(Properties.EVENT_TYPE, original.getEventType().toString());
        properties.put(Properties.HANDLER, original.getHandler());
        properties.put(Properties.ELEMENT, original.getSource().toString());
        properties.put(Properties.STATUS, original.getStatus().toString());
        properties.put(Properties.IS_NAV, (original.isNavigation() ? BOOLEAN_TRUE : BOOLEAN_FALSE));
        properties.put(Properties.PARENT, original.getParent().toString());

        if (original.getStatus().equals(Eventable.Status.FAILED)) {
            properties.put(Properties.REASON_FOR_FAILRE, original.getReasonForFailure());
        }

        return properties;
    }

    @Override
    public Eventable translateFrom(Map<String, Object> translated) {
        Element source = new Element((String)translated.get(Properties.ELEMENT));
        Document parent = new Document((String)translated.get(Properties.PARENT));
        Eventable.EventType type = Eventable.EventType.valueOf((String)translated.get(Properties.EVENT_TYPE));

        Eventable event = new ExecutableEvent();
        event.setId((String)translated.get(Properties.IDENTIFICATION));
        event.setXpath((String)translated.get(Properties.XPATH));
        event.setHandler((String)translated.get(Properties.HANDLER));
        event.setStatus(Eventable.Status.valueOf((String)translated.get(Properties.STATUS)));
        event.setReasonForFailure((String)translated.get(Properties.REASON_FOR_FAILRE));
        event.setEventType(type);
        event.setSource(source);
        event.setParent(parent);
        event.setIsNavigation(translated.get(Properties.IS_NAV).equals(BOOLEAN_TRUE));

        return event;
    }
}
