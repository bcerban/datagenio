package com.datagenio.crawler.model;

import com.datagenio.crawler.api.Eventable;
import com.datagenio.crawler.api.EventableExtractor;
import com.datagenio.crawler.api.ExecutedEventable;
import com.datagenio.crawler.api.State;
import com.datagenio.crawler.exception.UncrawlableStateException;
import org.jsoup.nodes.Document;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Queue;

public class StateImpl implements State {

    private Collection<ExecutedEventable> executedEventables;
    private Queue<Eventable> unfiredEventables;
    private Collection<Eventable> eventables;
    private Document document;
    private URI uri;

    public StateImpl(URI uri, Document view, EventableExtractor extractor) {
        this.uri = uri;
        this.document = view;
        this.eventables = extractor.extract(this, this.document);
        this.unfiredEventables = new LinkedList<>(this.eventables);
        this.executedEventables = new ArrayList<>();
    }

    @Override
    public Collection<Eventable> getEventables() {
        return eventables;
    }

    @Override
    public Collection<Eventable> getUnfiredEventables() {
        return this.unfiredEventables;
    }

    @Override
    public Eventable getNextEventToFire() throws UncrawlableStateException {
        Eventable next = null;
        while(next == null && !this.unfiredEventables.isEmpty()) {
            var event = this.unfiredEventables.poll();
            if (this.eventables.contains(event)) {
                next = event;
            }
        }

        if (next == null) {
            throw new UncrawlableStateException("This state is inconsistent! Looks unfinished but has no unfired events.");
        }

        return next;
    }

    @Override
    public boolean isFinished() {
        return this.unfiredEventables.isEmpty();
    }

    @Override
    public void setEventables(Collection<Eventable> eventables) {
        this.eventables = eventables;
    }

    @Override
    public void markEventAsFired(ExecutedEventable event) {
        this.unfiredEventables.remove(event.getEvent());
        this.executedEventables.add(event);
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
