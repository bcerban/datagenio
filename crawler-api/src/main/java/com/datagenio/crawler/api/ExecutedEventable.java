package com.datagenio.crawler.api;

import com.datagenio.context.EventInput;

import java.util.List;
import java.util.Map;

public interface ExecutedEventable {
    Eventable getEvent();
    List<EventInput> getDataInputs();

    void setEvent(Eventable event);
    void setDataInputs(List<EventInput> inputs);
}
