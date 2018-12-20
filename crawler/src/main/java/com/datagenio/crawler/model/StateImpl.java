package com.datagenio.crawler.model;

import com.datagenio.crawler.api.Eventable;
import com.datagenio.crawler.api.EventableExtractor;
import com.datagenio.crawler.api.State;
import org.jsoup.nodes.Document;

import java.util.Collection;

public class StateImpl implements State {

    private Collection<Eventable> eventables;
    private Document document;

    public StateImpl(Document view, EventableExtractor extractor) {
        this.document = view;
        this.eventables = extractor.extract(this, this.document);
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

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        StateImpl s = (StateImpl) obj;
        return this.getEventables().equals(s.getEventables());
    }
}
