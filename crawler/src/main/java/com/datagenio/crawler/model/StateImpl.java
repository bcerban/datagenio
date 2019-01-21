package com.datagenio.crawler.model;

import com.datagenio.crawler.api.Eventable;
import com.datagenio.crawler.api.EventableExtractor;
import com.datagenio.crawler.api.ExecutedEventable;
import com.datagenio.crawler.api.State;
import com.datagenio.crawler.exception.UncrawlableStateException;
import org.jsoup.nodes.Document;

import java.io.File;
import java.net.URI;
import java.util.*;

public class StateImpl implements State {

    private Collection<ExecutedEventable> executedEventables;
    private Queue<Eventable> unfiredEventables;
    private Collection<Eventable> eventables;
    private Document document;
    private URI uri;
    private String handler;
    private File screenShot;
    private final String uid;

    public StateImpl(URI uri, Document view, EventableExtractor extractor) {
        this.uri = uri;
        this.document = view;
        this.eventables = extractor.extract(this, this.document);
        this.unfiredEventables = new LinkedList<>(this.eventables);
        this.executedEventables = new ArrayList<>();
        this.uid = UUID.randomUUID().toString();
    }

    public StateImpl(URI uri, Document view, EventableExtractor extractor, String handler) {
        this(uri, view, extractor);
        this.handler = handler;
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
    public File getScreenShot() {
        return screenShot;
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
    public void setScreenShot(File screenshot) {
        this.screenShot = screenshot;
    }

    @Override
    public String getIdentifier() {
        return this.uid;
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
    public String getHandler() {
        return handler;
    }

    @Override
    public void setHandler(String handler) {
        this.handler = handler;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        StateImpl s = (StateImpl) obj;
        return this.getEventables().equals(s.getEventables());
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.getEventables());
    }
}
