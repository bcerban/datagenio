package com.datagenio.crawler.api;

import org.jsoup.nodes.Element;

public interface Eventable {

    enum EventType {
        click, submit
    }

    Element getSource();
    String getIdentifier();
    String getXpath();
    EventType getEventType();
    String getHandler();

    void setSource(Element source);
    void setEventType(EventType eventType);
    void setHandler(String handler);
}
