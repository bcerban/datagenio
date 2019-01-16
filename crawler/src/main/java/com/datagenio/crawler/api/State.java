package com.datagenio.crawler.api;

import org.jsoup.nodes.Document;

import java.net.URI;
import java.util.Collection;

public interface State {

    URI getUri();
    Document getDocument();
    Collection<Eventable> getEventables();
    Collection<Eventable> getUnfiredEventables();
    boolean isFinished();

    void setUri(URI uri);
    void setDocument(Document document);
    void setEventables(Collection<Eventable> eventables);
    void markEventAsFired(ExecutedEventable event);
}
