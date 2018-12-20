package com.datagenio.crawler.model;

import com.datagenio.crawler.api.Eventable;
import com.datagenio.crawler.api.EventableExtractor;
import org.jsoup.nodes.Document;

import java.util.Collection;

public class State {

    private Collection<Eventable> eventables;
    private Document document;

    public State(Document view, EventableExtractor extractor) {
        this.document = view;
        this.eventables = extractor.extract(this.document);
    }

    public Collection<Eventable> getEventables() {
        return eventables;
    }

    public void setEventables(Collection<Eventable> eventables) {
        this.eventables = eventables;
    }

    public Document getDocument() {
        return document;
    }

    public void setDocument(Document document) {
        this.document = document;
    }
}
