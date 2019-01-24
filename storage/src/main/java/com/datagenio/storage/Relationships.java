package com.datagenio.storage;

import org.neo4j.graphdb.RelationshipType;

public enum Relationships implements RelationshipType {
    WEB_TRANSITION,
    EXECUTED_EVENT,
    LEADS_TO,
    HAS_REQUEST,
    ABSTRACTED
}
