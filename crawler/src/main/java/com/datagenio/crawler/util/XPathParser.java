package com.datagenio.crawler.util;

import org.jsoup.nodes.Element;

public class XPathParser {

    public static String getXPathFor(Element e) {
        String xpath = "";
        Element current = e;

        while (current != null && !current.tagName().equals("#root")) {
            String tag = current.tagName();

            if (hasSiblingsWithSameTag(current)) {
                var index = current.elementSiblingIndex() + 1;
                tag = tag + '[' + index + ']';
            }

            if (!xpath.isEmpty()) {
                tag += '/';
            }

            xpath = tag + xpath;
            current = current.parent();
        }

        return '/' + xpath;
    }

    /**
     * @param e The element whose siblings are checked
     * @return True if the element has a sibling with the same tag, False otherwise.
     */
    private static boolean hasSiblingsWithSameTag(Element e) {
        return e.siblingElements().is(e.tagName());
    }
}
