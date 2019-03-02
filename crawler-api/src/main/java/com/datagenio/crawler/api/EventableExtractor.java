package com.datagenio.crawler.api;

import org.jsoup.nodes.Element;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;

public interface EventableExtractor {

    List<Eventable> extract(State origin, Element node);
    List<Eventable> extractShuffled(State origin, Element node);
    List<Eventable> extractSorted(State origin, Element node, Comparator<Eventable> comparator);
    Element findSubmittableChild(Element element);
    Collection<ExtractionRule> getRules();
    void setRules(Collection<ExtractionRule> rules);
}
