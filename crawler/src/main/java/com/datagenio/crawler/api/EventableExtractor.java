package com.datagenio.crawler.api;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.util.Collection;

public interface EventableExtractor {

    Collection<Eventable> extract(State origin, Element node);
}
