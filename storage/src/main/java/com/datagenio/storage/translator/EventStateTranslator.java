package com.datagenio.storage.translator;

import com.datagenio.crawler.api.State;
import com.datagenio.crawler.model.StateImpl;
import com.datagenio.model.WebStateImpl;
import com.datagenio.model.api.AbstractHttpRequest;
import com.datagenio.model.api.AbstractUrl;
import com.datagenio.model.api.WebState;
import com.datagenio.storageapi.Properties;
import com.datagenio.storageapi.Translator;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.jsoup.nodes.Document;
import org.neo4j.graphdb.Node;

import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class EventStateTranslator implements Translator<State, Map<String, Object>> {

    private Gson gson;

    public EventStateTranslator() {
        gson = new GsonBuilder().setPrettyPrinting().create();
    }

    @Override
    public Map<String, Object> buildProperties(State original) {
        Map<String, Object> properties = new HashMap<>();
        properties.put(Properties.IDENTIFICATION, original.getIdentifier());
        properties.put(Properties.IS_ROOT, original.isRoot() ? BOOLEAN_TRUE : BOOLEAN_FALSE);
        properties.put(Properties.URL, original.getUri().toString());
        properties.put(Properties.IS_FINISHED, original.isFinished() ? BOOLEAN_TRUE : BOOLEAN_FALSE);
        properties.put(Properties.DOCUMENT, original.getDocument().toString());

        if (original.hasScreenShot()) {
            properties.put(Properties.SCREEN_SHOT_PATH, original.getScreenShot().getAbsolutePath());
        }

        return properties;
    }

    @Override
    public State translateFrom(Map<String, Object> translated) {
        Document document = new Document((String)translated.get(Properties.DOCUMENT));

        State state = new StateImpl();
        state.setIdentifier((String)translated.get(Properties.IDENTIFICATION));
        state.setUri(URI.create((String)translated.get(Properties.URL)));
        state.setDocument(document);
        state.setScreenShot(new File((String)translated.get(Properties.SCREEN_SHOT_PATH)));
        state.setIsRoot(translated.get(Properties.IS_ROOT).equals(BOOLEAN_TRUE));

        return state;
    }
}
