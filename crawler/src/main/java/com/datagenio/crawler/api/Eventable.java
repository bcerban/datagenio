package com.datagenio.crawler.api;

import org.jsoup.nodes.Element;

public interface Eventable {

    enum EventType {
        click, submit
    }

    State getOrigin();
    State getDestination();
    Element getSource();
    String getIdentifier();
    String getXpath();
    EventType getEventType();

    void setOrigin(State origin);
    void setDestination(State destination);
    void setSource(Element source);
    void setEventType(EventType eventType);
}
