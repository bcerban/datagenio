package com.datagenio.databank.util;

import org.apache.commons.lang.StringUtils;
import org.jsoup.nodes.Element;

public class InputSelector {

    public static String DISABLED = "disabled";
    public static String READ_ONLY = "readonly";
    public static String INPUT_SELECTOR = "input, textarea, select";

    public static boolean isInput(Element element) {
        String disabled = element.attr(DISABLED);
        String readOnly = element.attr(READ_ONLY);
        if (StringUtils.isNotBlank(disabled) && disabled.equals("true")) return false;
        if (StringUtils.isNotBlank(readOnly) && disabled.equals("true")) return false;

        return element.is(INPUT_SELECTOR);
    }
}
