package com.datagenio.crawler.api;

import org.jsoup.nodes.Element;

public interface Eventable {

    enum EventType {
        click, submit, hover
    }

    enum Status {
        NOT_EXECUTED, SUCCEEDED, FAILED
    }

    Element getSource();
    String getIdentifier();
    String getXpath();
    EventType getEventType();
    String getHandler();
    boolean isNavigation();
    Status getStatus();
    String getReasonForFailure();

    void setSource(Element source);
    void setEventType(EventType eventType);
    void setHandler(String handler);
    void setIsNavigation(boolean isNavigation);
    void setStatus(Status status);
    void setReasonForFailure(String reasonForFailure);
}
