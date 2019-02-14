package com.datagenio.crawler.model;

import com.datagenio.crawler.api.Eventable;
import com.datagenio.crawler.api.EventableExtractor;
import com.datagenio.crawler.api.ExecutedEventable;
import com.datagenio.crawler.api.State;
import com.datagenio.crawler.exception.UncrawlableStateException;
import com.datagenio.crawler.util.SubmitFirstComparator;
import org.jsoup.nodes.Document;

import java.io.File;
import java.net.URI;
import java.util.*;
import java.util.stream.Collectors;

public class StateImpl implements State {

    private Collection<ExecutedEventable> executedEventables;
    private Queue<Eventable> unfiredEventables;
    private Collection<Eventable> eventables;
    private Document document;
    private URI uri;
    private File screenShot;
    private boolean isRoot;
    private String uid;
    private String documentFilePath;

    public StateImpl() {
        eventables = new ArrayList<>();
        unfiredEventables = new LinkedList<>();
        executedEventables = new ArrayList<>();
    }

    public StateImpl(URI uri, Document view, EventableExtractor extractor) {
        this.uri = uri;
        this.document = view;
        this.eventables = extractor.extractSorted(this, document, new SubmitFirstComparator());
        this.unfiredEventables = new LinkedList<>(eventables);
        this.executedEventables = new ArrayList<>();
        this.uid = UUID.randomUUID().toString();
    }

    @Override
    public Collection<Eventable> getEventables() {
        return eventables;
    }

    @Override
    public Collection<Eventable> getUnfiredEventables() {
        return unfiredEventables;
    }

    @Override
    public void setUnfiredEventables(Collection<Eventable> eventables) {
        List<Eventable> sorted = new ArrayList<>(eventables);
        Collections.sort(sorted, new SubmitFirstComparator());
        unfiredEventables = new LinkedList<>(sorted);
    }

    @Override
    public Eventable getNextEventToFire() throws UncrawlableStateException {
        Eventable next = null;
        while(next == null && !unfiredEventables.isEmpty()) {
            var event = unfiredEventables.poll();
            if (eventables.contains(event)) {
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
        return unfiredEventables.isEmpty();
    }

    @Override
    public boolean isRoot() {
        return isRoot;
    }

    @Override
    public File getScreenShot() {
        return screenShot;
    }

    @Override
    public String getDocumentFilePath() {
        return documentFilePath;
    }

    @Override
    public boolean hasScreenShot() {
        return screenShot != null;
    }

    @Override
    public void setEventables(Collection<Eventable> eventables) {
        this.eventables = eventables;
    }

    @Override
    public void markEventAsFired(ExecutedEventable event) {
        unfiredEventables.remove(event.getEvent());
        executedEventables.add(event);
    }

    @Override
    public void setScreenShot(File screenshot) {
        this.screenShot = screenshot;
    }

    @Override
    public void setIsRoot(boolean isRoot) {
        this.isRoot = isRoot;
    }

    @Override
    public void setDocumentFilePath(String path) {
        this.documentFilePath = path;
    }

    @Override
    public String getIdentifier() {
        return this.uid;
    }

    @Override
    public void setIdentifier(String identifier) {
        uid = identifier;
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
        return getEventables().equals(s.getEventables());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getEventables());
    }
}
