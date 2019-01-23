package com.datagenio.crawler.util;

import com.datagenio.crawler.api.EventableExtractor;
import com.datagenio.crawler.api.ExtractionRule;
import com.datagenio.crawler.rule.AnchorRule;
import com.datagenio.crawler.rule.TagRule;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class EventExtractorFactory {

    public static EventableExtractor get() {
        return new ExecutableEventExtractor(buildRules());
    }

    private static Collection<ExtractionRule> buildRules() {
        // TODO: inject dependencies, make configurable
        var rules = new ArrayList<ExtractionRule>();
        rules.add(new AnchorRule());
        rules.add(new TagRule(List.of("button", "form")));

        return rules;
    }
}
