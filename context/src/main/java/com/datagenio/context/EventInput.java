package com.datagenio.context;

import com.google.gson.annotations.SerializedName;

public class EventInput {

    @SerializedName("event_id")
    private String eventId;

    @SerializedName("xpath")
    private String xpath;

    @SerializedName("input_type")
    private String inputType;

    @SerializedName("input_value")
    private String inputValue;

    @SerializedName("input_required")
    private String required;

    public String getXpath() {
        return xpath;
    }

    public void setXpath(String xpath) {
        this.xpath = xpath;
    }

    public String getInputType() {
        return inputType;
    }

    public void setInputType(String inputType) {
        this.inputType = inputType;
    }

    public String getInputValue() {
        return inputValue;
    }

    public void setInputValue(String inputValue) {
        this.inputValue = inputValue;
    }

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public String getRequired() {
        return required;
    }

    public void setRequired(String required) {
        this.required = required;
    }
}
