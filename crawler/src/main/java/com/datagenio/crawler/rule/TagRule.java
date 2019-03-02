package com.datagenio.crawler.rule;

import com.datagenio.crawler.api.ExtractionRule;
import com.datagenio.crawler.util.ExecutableEventExtractor;
import org.jsoup.nodes.Element;

import java.util.Collection;

public class TagRule implements ExtractionRule {

    private Collection<String> allowedTags;

    public TagRule(Collection<String> tags) {
        this.allowedTags = tags;
    }

    public Collection<String> getAllowedTags() {
        return allowedTags;
    }

    public void setAllowedTags(Collection<String> allowedTags) {
        this.allowedTags = allowedTags;
    }

    @Override
    public boolean matches(Element element) {
        String role = element.attr(ExecutableEventExtractor.ATTR_ROLE);
        return allowedTags.contains(element.tagName()) && !ExecutableEventExtractor.ROLE_MODAL.equals(role.toLowerCase());
    }
}
