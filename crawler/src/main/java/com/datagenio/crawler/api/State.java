package com.datagenio.crawler.api;

import org.jsoup.nodes.Document;

import java.util.Collection;

public interface State {

    Document getDocument();
    Collection<Eventable> getEventables();

    void setDocument(Document document);
    void setEventables(Collection<Eventable> eventables);

}
