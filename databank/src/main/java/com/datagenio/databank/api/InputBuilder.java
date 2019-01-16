package com.datagenio.databank.api;

import java.util.Map;
import org.jsoup.nodes.Element;

public interface InputBuilder {
    static String DEFAULT = "default";

    Map<String, String> buildInputs(Element element);
}
