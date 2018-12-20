package com.datagenio.crawler;

import com.datagenio.crawler.api.Eventable;
import com.datagenio.crawler.api.EventableExtractor;
import com.datagenio.crawler.model.ExecutableEvent;
import org.jsoup.nodes.Document;

import java.util.ArrayList;
import java.util.Collection;

public class ConfigurableExtractor implements EventableExtractor {

    public Collection<Eventable> extract(Document view) {
        var eventables = new ArrayList<Eventable>();

//        var nodes = view.body();
        return eventables;
    }
}
