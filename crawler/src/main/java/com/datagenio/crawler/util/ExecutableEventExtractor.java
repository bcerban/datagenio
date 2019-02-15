package com.datagenio.crawler.util;

import com.datagenio.crawler.api.Eventable;
import com.datagenio.crawler.api.EventableExtractor;
import com.datagenio.crawler.api.ExtractionRule;
import com.datagenio.crawler.api.State;
import com.datagenio.crawler.model.ExecutableEvent;
import org.jsoup.nodes.Element;

import java.util.*;

public class ExecutableEventExtractor implements EventableExtractor {

    private Collection<ExtractionRule> rules;

    public ExecutableEventExtractor(Collection<ExtractionRule> rules) {
        this.rules = rules;
    }

    public Collection<ExtractionRule> getRules() {
        return rules;
    }

    public void setRules(Collection<ExtractionRule> rules) {
        this.rules = rules;
    }

    public Collection<Eventable> extract(State origin, Element node) {
        var eventables = new ArrayList<Eventable>();

        if (isEventableElement(node)) {
            var eventable = new ExecutableEvent(node, getEventTypeFor(node));
            eventable.setIsNavigation(isNavigationElement(node));
            eventables.add(eventable);
        } else {
            node.children().forEach(child -> eventables.addAll(extract(origin, child)));
        }

        return eventables;
    }

    @Override
    public Collection<Eventable> extractSorted(State origin, Element node, Comparator comparator) {
        List<Eventable> events = new ArrayList<>(extract(origin, node));
        Collections.sort(events, comparator);
        return events;
    }

    public Element findSubmitableChild(Element element) {
        if (isSubmitableElement(element)) {
            return element;
        }

        Element found = null;
        var iterator = element.children().iterator();
        while (iterator.hasNext() && found == null) {
            var child = iterator.next();
            found = findSubmitableChild(child);
        }

        return found;
    }

    private boolean isSubmitableElement(Element element) {
        boolean isSubmitable = false;

        String type = element.attr("type");
        String action = element.attr("action");
        String classes = element.className();

        if ((element.is("input") || element.is("button"))
                && (type.contains("submit") || action.contains("submit") || classes.contains("submit"))) {
            isSubmitable = true;
        }

        return isSubmitable;
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
            return Eventable.EventType.SUBMIT;
        }

        return Eventable.EventType.CLICK;
    }

    private boolean isNavigationElement(Element element) {
        String matcher = "nav, header";
        if (element.is(matcher)) return true;

        Element current = element;
        while (current.hasParent()) {
            current = current.parent();
            if (current.is(matcher)) return true;
        }

        return false;
    }
}
