package com.datagenio.storage.translator;

import com.datagenio.crawler.api.Eventable;
import com.datagenio.crawler.model.ExecutableEvent;
import com.datagenio.storageapi.Properties;
import com.datagenio.storageapi.Translator;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.neo4j.graphdb.Node;

import java.util.HashMap;
import java.util.Map;

public class EventTranslator implements Translator<Eventable, Node> {

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
    public Eventable translateFrom(Node translated) {
        Element source = new Element((String)translated.getProperty(Properties.ELEMENT));
        Document parent = new Document((String)translated.getProperty(Properties.PARENT));
        Eventable.EventType type = Eventable.EventType.valueOf((String)translated.getProperty(Properties.EVENT_TYPE));

        Eventable event = new ExecutableEvent();
        event.setId((String)translated.getProperty(Properties.IDENTIFICATION));
        event.setXpath((String)translated.getProperty(Properties.XPATH));
        event.setHandler((String)translated.getProperty(Properties.HANDLER));
        event.setStatus(Eventable.Status.valueOf((String)translated.getProperty(Properties.STATUS)));
        event.setReasonForFailure((String)translated.getProperty(Properties.REASON_FOR_FAILRE));
        event.setEventType(type);
        event.setSource(source);
        event.setParent(parent);
        event.setIsNavigation(translated.getProperty(Properties.IS_NAV).equals(BOOLEAN_TRUE));

        return event;
    }
}
