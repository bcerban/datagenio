package com.datagenio.storageapi;

public interface Properties {
    // State Properties
    String IDENTIFICATION = "identifier";
    String IS_ROOT = "is_root";
    String SCREEN_SHOT_PATH = "screen_shot";
    String SCREEN_SHOTS = "screen_shots";
    String URL = "url";
    String IS_FINISHED = "is_finished";
    String EXTERNAL_IDS = "external_ids";
    String DOCUMENT = "document";

    // WebTransition Properties
    String ABSTRACT_REQUESTS = "abstract_requests";
    String CONCRETE_REQUESTS = "concrete_requests";

    // Event Transition properties
    String EXECUTED_EVENT_ID = "executed_event";
    String STATUS = "status";
    String REASON_FOR_FAILRE = "reason_for_failure";
    String DATA_INPUTS = "data_inputs";

    // Event properties
    String EVENT_IDENTIFICATION = "event_identifier";
    String XPATH = "xpath";
    String EVENT_TYPE = "event_type";
    String ELEMENT = "element";
    String HANDLER = "handler";
    String IS_NAV = "is_navigation";
    String PARENT = "parent_doc";

    // Request properties
    String REQUEST_JSON = "json";
}
