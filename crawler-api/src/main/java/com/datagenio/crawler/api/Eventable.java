package com.datagenio.crawler.api;

import org.jsoup.nodes.Element;

public interface Eventable {

    enum EventType {
        CLICK, SUBMIT, HOVER
    }

    enum Status {
        NOT_EXECUTED, SUCCEEDED, FAILED
    }

    String getId();
    Element getSource();

    /**
     * Returns a copy of the source, with cleared styles
     * and input values.
     *
     * @return the stripped source element
     */
    Element getStrippedSource();
    String getEventIdentifier();
    String getXpath();
    EventType getEventType();
    String getHandler();
    boolean isNavigation();
    boolean requiresInput();
    Status getStatus();
    String getReasonForFailure();

    void setId(String id);
    void setSource(Element source);
    void setEventType(EventType eventType);
    void setHandler(String handler);
    void setIsNavigation(boolean isNavigation);
    void setStatus(Status status);
    void setReasonForFailure(String reasonForFailure);
    void setXpath(String xpath);
}
