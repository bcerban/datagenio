package com.datagenio.crawler.model;

import com.datagenio.crawler.XPathParser;
import com.datagenio.crawler.api.Eventable;
import org.apache.commons.lang.StringUtils;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Document;

import java.util.Objects;

/**
 * Represents an event that can be executed from an instance of the
 * application's user interface.
 */
public class ExecutableEvent implements Eventable {

    private Element source;
    private EventType eventType;
    private String handler = "";
    private String xpath;

    /** Not sure whether saving the full DOM is necessary, but it can be removed later. */
    private Document parent;

    public ExecutableEvent(Element e, EventType event) {
        this.source = e;
        this.eventType = event;
        this.parent = this.source.ownerDocument();
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

    public String getHandler() {
        if (StringUtils.isEmpty(this.handler) && this.source.hasAttr("action")) {
            this.handler = this.source.attr("action");
        }
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

    @Override
    public String getXpath() {
        if (StringUtils.isEmpty(this.xpath)) {
            this.xpath = XPathParser.getXPathFor(this.source);
        }
        return xpath;
    }

    @Override
    public String getIdentifier() {
        String identifier = this.source.id();

        if (StringUtils.isEmpty(identifier)) {
            identifier = this.getXpath();
        }
        return identifier;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        ExecutableEvent e = (ExecutableEvent) obj;
        return Objects.equals(getEventType(), e.getEventType()) &&
                Objects.equals(getIdentifier(), e.getIdentifier()) &&
                Objects.equals(getHandler(), e.getHandler());
    }
}
