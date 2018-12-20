package com.datagenio.crawler;

import com.datagenio.crawler.model.Eventable;
import org.jsoup.nodes.Document;

import java.util.ArrayList;
import java.util.Collection;

public class EventableExtractor {

    public static Collection<Eventable> extract(Document view) {
        var eventables = new ArrayList<Eventable>();

        var nodes = view.body();
        return eventables;
    }
}
