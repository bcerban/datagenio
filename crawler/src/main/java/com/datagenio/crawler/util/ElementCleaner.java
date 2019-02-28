package com.datagenio.crawler.util;

import com.datagenio.databank.util.InputSelector;
import org.jsoup.nodes.Element;

public class ElementCleaner {

    public static final String ATTR_STYLE = "style";
    public static final String ATTR_VALUE = "value";

    public static Element clean(Element node) {
        Element stripped = node.clone();
        clearElementStyles(stripped);
        clearElementInputs(stripped);
        return stripped;
    }

    /**
     * Remove the style attribute from node and all its children recursively.
     * @param node
     */
    private static void clearElementStyles(Element node) {
        if (node.hasAttr(ATTR_STYLE)) node.removeAttr(ATTR_STYLE);
        if (node.childNodeSize() > 0) node.children().forEach(child -> clearElementStyles(child));
    }

    /**
     * Set the attribute value to "" if the element had a value attribute.
     * Do this recursively for node, and all its children.
     *
     * @param node
     */
    private static void clearElementInputs(Element node) {
        if (InputSelector.isInput(node)) {
            if (node.hasAttr(ATTR_VALUE)) node.attr(ATTR_VALUE, "");
        } else {
            node.children().forEach(child -> clearElementInputs(child));
        }
    }
}
