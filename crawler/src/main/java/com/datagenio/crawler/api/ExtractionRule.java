package com.datagenio.crawler.api;

import org.jsoup.nodes.Element;

public interface ExtractionRule {

    boolean matches(Element element);
}
