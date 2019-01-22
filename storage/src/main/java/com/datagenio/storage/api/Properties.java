package com.datagenio.storage.api;

public interface Properties {
    // State Properties
    String IDENTIFICATION = "identifier";
    String IS_ROOT = "is_root";
    String SCREEN_SHOT_PATH = "screen_shot";

    // WebTransition Properties
    String ABSTRACT_REQUESTS = "abstract_requests";
    String CONCRETE_REQUESTS = "concrete_requests";

    // Event Transition properties
    String EXECUTED_EVENT_ID = "executed_event";

    // Event properties
    String XPATH = "xpath";
    String EVENT_TYPE = "event_type";
    String ELEMENT = "element";

    // Request properties
    String REQUEST_JSON = "json";
}
