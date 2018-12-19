package com.datagenio.crawler.model;

import org.apache.commons.lang.StringUtils;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Document;

import java.util.Objects;

/**
 * Represents an event that can be executed from an instance of the
 * application's user interface.
 */
public class Eventable {

    public enum EventType {
        click, submit
    }

    private Element source;
    private EventType eventType;
    private String handler = "";
    private String xpath;

    /** Not sure whether saving the full DOM is necessary, but it can be removed later. */
    private Document parent;

    public Eventable(Element e, EventType event) {
        this.source = e;
        this.eventType = event;
        this.parent = this.source.ownerDocument();

        if (this.source.hasAttr("action")) {
            this.handler = this.source.attr("action");
        }

        this.xpath = XPathParser.getXPathFor(this.source);
    }

    public Element getSource() {
        return source;
    }

    public void setSource(Element source) {
        this.source = source;
    }

    public EventType getEventType() {
        return eventType;
    }

    public void setEventType(EventType eventType) {
        this.eventType = eventType;
    }

    public String getHandler() {
        return handler;
    }

    public void setHandler(String handler) {
        this.handler = handler;
    }

    public Document getParent() {
        return parent;
    }

    public void setParent(Document parent) {
        this.parent = parent;
    }

    public String getXpath() {
        return xpath;
    }

    public String getSourceIdentifier() {
        String identifier = this.source.id();

        if (StringUtils.isEmpty(identifier)) {
            identifier = this.xpath;
        }
        return identifier;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        Eventable e = (Eventable) obj;
        return Objects.equals(getEventType(), e.getEventType()) &&
                Objects.equals(getSourceIdentifier(), e.getSourceIdentifier()) &&
                Objects.equals(getHandler(), e.getHandler());
    }
}
