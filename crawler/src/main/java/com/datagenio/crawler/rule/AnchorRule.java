package com.datagenio.crawler.rule;

import com.datagenio.crawler.api.ExtractionRule;
import org.jsoup.nodes.Element;

public class AnchorRule implements ExtractionRule {

    public static final String A_TAG = "a";
    public static final String HREF = "href";
    public static final String MAIL_TO_REGEX = "^mailto.*";

    @Override
    public boolean matches(Element element) {
        return element.tagName().equals(A_TAG) && hrefIsAllowed(element);
    }

    private boolean hrefIsAllowed(Element element) {
        String href = element.attributes().get(HREF);
        return !href.matches(MAIL_TO_REGEX);
    }
}
