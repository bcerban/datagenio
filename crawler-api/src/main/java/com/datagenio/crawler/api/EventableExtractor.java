package com.datagenio.crawler.api;

import org.jsoup.nodes.Element;
import java.util.Collection;
import java.util.Comparator;

public interface EventableExtractor {

    Collection<Eventable> extract(State origin, Element node);
    Collection<Eventable> extractSorted(State origin, Element node, Comparator<Eventable> comparator);
    Element findSubmitableChild(Element element);
    Collection<ExtractionRule> getRules();
    void setRules(Collection<ExtractionRule> rules);
}
