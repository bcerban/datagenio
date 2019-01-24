package com.datagenio.storage.api;

public interface Properties {
    // State Properties
    String IDENTIFICATION = "identifier";
    String IS_ROOT = "is_root";
    String SCREEN_SHOT_PATH = "screen_shot";
    String SCREEN_SHOTS = "screen_shots";
    String URL = "url";
    String FINISHED = "finished";
    String UNFINISHED = "unfinished";
    String EXTERNAL_IDS = "external_ids";

    // WebTransition Properties
    String ABSTRACT_REQUESTS = "abstract_requests";
    String CONCRETE_REQUESTS = "concrete_requests";

    // Event Transition properties
    String EXECUTED_EVENT_ID = "executed_event";
    String STATUS = "status";
    String REASON_FOR_FAILRE = "reason_for_failure";

    // Event properties
    String XPATH = "xpath";
    String EVENT_TYPE = "event_type";
    String ELEMENT = "element";

    // Request properties
    String REQUEST_JSON = "json";
}
