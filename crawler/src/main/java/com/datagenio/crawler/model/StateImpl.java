package com.datagenio.crawler.model;

import com.datagenio.crawler.api.Eventable;
import com.datagenio.crawler.api.EventableExtractor;
import com.datagenio.crawler.api.State;
import org.jsoup.nodes.Document;

import java.net.URI;
import java.util.Collection;

public class StateImpl implements State {

    private Collection<Eventable> eventables;
    private Document document;
    private URI uri;

    public StateImpl(URI uri, Document view, EventableExtractor extractor) {
        this.uri = uri;
        this.document = view;
        this.eventables = extractor.extract(this, this.document);
    }

    @Override
    public Collection<Eventable> getEventables() {
        return eventables;
    }

    @Override
    public void setEventables(Collection<Eventable> eventables) {
        this.eventables = eventables;
    }

    @Override
    public URI getUri() {
        return this.uri;
    }

    @Override
    public void setUri(URI uri) {
        this.uri = uri;
    }

    @Override
    public Document getDocument() {
        return document;
    }

    @Override
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
