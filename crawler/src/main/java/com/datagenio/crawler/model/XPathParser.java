package com.datagenio.crawler.model;

import org.jsoup.nodes.Element;

public class XPathParser {

    public static String getXPathFor(Element e) {
        String xpath = e.tagName();
        Element current = e.parent();

        while (current != null) {
            xpath = current.tagName() + '/' + xpath;
            current = current.parent();
        }

        return xpath;
    }
}
