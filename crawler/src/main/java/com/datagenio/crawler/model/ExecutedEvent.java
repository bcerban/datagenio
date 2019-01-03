package com.datagenio.crawler.model;

import com.datagenio.crawler.api.Eventable;
import com.datagenio.crawler.api.ExecutedEventable;

import java.util.HashMap;
import java.util.Map;

public class ExecutedEvent implements ExecutedEventable {

    private Eventable event;
    private Map<String, String> inputs;

    public ExecutedEvent(Eventable event) {
        this.event = event;
        this.inputs = new HashMap<>();
    }

    @Override
    public Eventable getEvent() {
        return this.event;
    }

    @Override
    public Map<String, String> getDataInputs() {
        return this.inputs;
    }

    @Override
    public void setEvent(Eventable event) {
        this.event = event;
    }

    @Override
    public void setDataInputs(Map<String, String> inputs) {
        this.inputs = inputs;
    }

    @Override
    public void addInput(String field, String value) {
        this.inputs.put(field, value);
    }
}
