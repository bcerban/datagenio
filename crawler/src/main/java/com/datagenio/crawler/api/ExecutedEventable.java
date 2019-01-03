package com.datagenio.crawler.api;

import java.util.Map;

public interface ExecutedEventable {
    Eventable getEvent();
    Map<String, String> getDataInputs();

    void setEvent(Eventable event);
    void setDataInputs(Map<String, String> inputs);

    void addInput(String field, String value);
}
