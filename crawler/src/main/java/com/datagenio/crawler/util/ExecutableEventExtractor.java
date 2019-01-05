package com.datagenio.crawler.util;

import com.datagenio.crawler.api.Eventable;
import com.datagenio.crawler.api.EventableExtractor;
import com.datagenio.crawler.api.ExtractionRule;
import com.datagenio.crawler.api.State;
import com.datagenio.crawler.model.ExecutableEvent;
import org.jsoup.nodes.Element;

import java.util.ArrayList;
import java.util.Collection;

public class ExecutableEventExtractor implements EventableExtractor {

    private Collection<ExtractionRule> rules;

    public ExecutableEventExtractor(Collection<ExtractionRule> rules) {
        this.rules = rules;
    }

    public Collection<Eventable> extract(State origin, Element node) {
        var eventables = new ArrayList<Eventable>();

        if (this.isEventableElement(node)) {
            eventables.add(new ExecutableEvent(node, getEventTypeFor(node)));
        } else {
            node.children().forEach(child -> eventables.addAll(extract(origin, child)));
        }

        return eventables;
    }

    private boolean isEventableElement(Element element) {
        var matched = false;
        var ruleIterator = rules.iterator();

        while(!matched && ruleIterator.hasNext()) {
            matched = ruleIterator.next().matches(element);
        }
        return matched;
    }

    private Eventable.EventType getEventTypeFor(Element element) {
        // TODO: Some Jsoup abstractions might be more appropriate here. Need to run some tests.
        if (element.tagName().equals("form")) {
            return Eventable.EventType.submit;
        }

        return Eventable.EventType.click;
    }
}
