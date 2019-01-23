package com.datagenio.crawler.rule;

import com.datagenio.crawler.api.ExtractionRule;
import org.jsoup.nodes.Element;

import java.util.Collection;

public class AnchorRule implements ExtractionRule {

    public static final String A_TAG = "a";
    public static final String MAIL_TO_REGEX = "^mailto.*";

    @Override
    public boolean matches(Element element) {
        return element.tagName().equals(A_TAG) && !element.tagName().matches(MAIL_TO_REGEX);
    }
}
