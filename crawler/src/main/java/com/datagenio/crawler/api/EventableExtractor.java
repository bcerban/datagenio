package com.datagenio.crawler.api;

import org.jsoup.nodes.Document;
import java.util.Collection;

public interface EventableExtractor {

    Collection<Eventable> extract(Document view);
}
