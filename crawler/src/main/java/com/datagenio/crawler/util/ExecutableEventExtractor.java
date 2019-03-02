package com.datagenio.crawler.util;

import com.datagenio.crawler.api.Eventable;
import com.datagenio.crawler.api.EventableExtractor;
import com.datagenio.crawler.api.ExtractionRule;
import com.datagenio.crawler.api.State;
import com.datagenio.crawler.model.ExecutableEvent;
import org.jsoup.nodes.Element;

import java.util.*;

public class ExecutableEventExtractor implements EventableExtractor {

    public static final String NAV_MATCHER = "nav, header";
    public static final String ATTR_STYLE = "style";
    public static final String ATTR_HIDDEN = "hidden";
    public static final String ATTR_ARIA_HIDDEN = "aria-hidden";
    public static final String ATTR_VISIBILITY_HIDDEN = "visibility: hidden";
    public static final String ATTR_ROLE = "role";
    public static final String ROLE_MODAL = "modal";

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

    @Override
    public List<Eventable> extract(State origin, Element node) {
        var eventables = new ArrayList<Eventable>();

        if (isEventableElement(node)) {
            var eventable = new ExecutableEvent(node, getEventTypeFor(node));
            eventable.setIsNavigation(isNavigationElement(node));
            eventables.add(eventable);
        } else {
            if (!isHidden(node)) node.children().forEach(child -> eventables.addAll(extract(origin, child)));
        }

        return eventables;
    }

    @Override
    public List<Eventable> extractShuffled(State origin, Element node) {
        List<Eventable> events = new ArrayList<>(extract(origin, node));
        Collections.shuffle(events);
        return events;
    }

    @Override
    public List<Eventable> extractSorted(State origin, Element node, Comparator comparator) {
        List<Eventable> events = new ArrayList<>(extract(origin, node));
        Collections.sort(events, comparator);
        return events;
    }

    public Element findSubmittableChild(Element element) {
        if (isSubmittableElement(element)) {
            return element;
        }

        Element found = null;
        var iterator = element.children().iterator();
        while (iterator.hasNext() && found == null) {
            var child = iterator.next();
            found = findSubmittableChild(child);
        }

        return found;
    }

    private boolean isSubmittableElement(Element element) {
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

    private boolean isHidden(Element element) {
        String style = element.attr(ATTR_STYLE);
        String role = element.attr(ATTR_ROLE);
        return element.hasAttr(ATTR_HIDDEN) || element.hasAttr(ATTR_ARIA_HIDDEN) || style.contains(ATTR_VISIBILITY_HIDDEN) || ROLE_MODAL.equals(role.toLowerCase());
    }

    private boolean isNavigationElement(Element element) {
        if (element.is(NAV_MATCHER)) return true;

        Element current = element;
        while (current.hasParent()) {
            current = current.parent();
            if (current.is(NAV_MATCHER)) return true;
        }

        return false;
    }
}
