package com.datagenio.crawler.model;

import com.datagenio.context.EventInput;
import com.datagenio.crawler.api.Eventable;
import com.datagenio.crawler.api.ExecutedEventable;

import java.util.ArrayList;
import java.util.List;

public class ExecutedEvent implements ExecutedEventable {

    private Eventable event;
    private List<EventInput> inputs;

    public ExecutedEvent(Eventable event) {
        this.event = event;
        this.inputs = new ArrayList<>();
    }

    public ExecutedEvent(Eventable event, List<EventInput> inputs) {
        this.event = event;
        this.inputs = inputs;
    }

    @Override
    public Eventable getEvent() {
        return this.event;
    }

    @Override
    public List<EventInput> getDataInputs() {
        return this.inputs;
    }

    @Override
    public void setEvent(Eventable event) {
        this.event = event;
    }

    @Override
    public void setDataInputs(List<EventInput> inputs) {
        this.inputs = inputs;
    }
}
