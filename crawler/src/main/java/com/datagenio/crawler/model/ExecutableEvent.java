package com.datagenio.crawler.model;

import com.datagenio.databank.util.XPathParser;
import com.datagenio.crawler.api.Eventable;
import org.apache.commons.lang.StringUtils;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Document;

import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.UUID;

/**
 * Represents an event that can be executed from an instance of the
 * application's user interface.
 */
public class ExecutableEvent implements Eventable {

    private String uid;
    private Element source;
    private EventType eventType;
    private String handler = "";
    private String xpath;
    private boolean isNav;
    private Status status = Status.NOT_EXECUTED;
    private String reasonForFailure = "";

    public ExecutableEvent() {}

    public ExecutableEvent(Element e, EventType event) {
        uid = UUID.randomUUID().toString();
        source = e;
        eventType = event;
        xpath = XPathParser.getXPathFor(source);
    }

    @Override
    public String getId() {
        return uid;
    }

    @Override
    public void setId(String id) {
        uid = id;
    }

    @Override
    public Element getSource() {
        return source;
    }

    @Override
    public void setSource(Element source) {
        this.source = source;
    }

    @Override
    public EventType getEventType() {
        return eventType;
    }

    @Override
    public void setEventType(EventType eventType) {
        this.eventType = eventType;
    }

    @Override
    public String getHandler() {
        if (StringUtils.isEmpty(handler) && source.hasAttr("action")) {
            handler = source.attr("action");
        }
        return handler;
    }

    @Override
    public void setHandler(String handler) {
        this.handler = handler;
    }

    @Override
    public void setXpath(String xpath) {
        this.xpath = xpath;
    }

    @Override
    public boolean isNavigation() {
        return isNav;
    }

    @Override
    public boolean requiresInput() {
        return getEventType().equals(EventType.SUBMIT);
    }

    @Override
    public void setIsNavigation(boolean isNavigation) {
        isNav = isNavigation;
    }

    @Override
    public String getXpath() {
        return xpath;
    }

    @Override
    public String getEventIdentifier() {
        String identifier = source.id();

        if (StringUtils.isEmpty(identifier)) {
            identifier = getXpath();
        }
        return identifier;
    }

    @Override
    public Status getStatus() {
        return status;
    }

    @Override
    public void setStatus(Status status) {
        this.status = status;
    }

    @Override
    public String getReasonForFailure() {
        return reasonForFailure;
    }

    @Override
    public void setReasonForFailure(String reasonForFailure) {
        this.reasonForFailure = reasonForFailure;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        ExecutableEvent e = (ExecutableEvent) obj;
        return getEventType().equals(e.getEventType()) &&
                getXpath().equals(e.getXpath()) &&
                getSource().toString().equals(e.getSource().toString());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getSource().toString());
    }
}
