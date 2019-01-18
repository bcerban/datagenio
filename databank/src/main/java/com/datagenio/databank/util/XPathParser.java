package com.datagenio.databank.util;

import org.jsoup.nodes.Element;

public class XPathParser {

    public static String getXPathFor(Element e) {
        String xpath = "";
        Element current = e;

        while (current != null && !current.tagName().equals("#root")) {
            String tag = current.tagName();

            if (hasSiblingsWithSameTag(current)) {
                tag = tag + '[' + getIndexAmongSiblingsWithSameTag(current) + ']';
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

    private static int getIndexAmongSiblingsWithSameTag(Element e) {
        int index = 1 + (int)e.siblingElements().stream()
                .limit(e.elementSiblingIndex())
                .filter((x) -> x.tagName().equals(e.tagName()))
                .count();
        return index;
    }
}
