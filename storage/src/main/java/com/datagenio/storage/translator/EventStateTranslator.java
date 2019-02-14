package com.datagenio.storage.translator;

import com.datagenio.crawler.api.State;
import com.datagenio.crawler.model.StateImpl;
import com.datagenio.storageapi.Properties;
import com.datagenio.storageapi.Translator;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

public class EventStateTranslator implements Translator<State, Map<String, Object>> {

    @Override
    public Map<String, Object> buildProperties(State original) {
        Map<String, Object> properties = new HashMap<>();
        properties.put(Properties.IDENTIFICATION, original.getIdentifier());
        properties.put(Properties.IS_ROOT, original.isRoot() ? BOOLEAN_TRUE : BOOLEAN_FALSE);
        properties.put(Properties.URL, original.getUri().toString());
        properties.put(Properties.IS_FINISHED, original.isFinished() ? BOOLEAN_TRUE : BOOLEAN_FALSE);

        if (original.getDocumentFilePath() != null) {
            properties.put(Properties.DOCUMENT_FILE, original.getDocumentFilePath());
        }

        if (original.hasScreenShot()) {
            properties.put(Properties.SCREEN_SHOT_PATH, original.getScreenShot().getAbsolutePath());
        }

        return properties;
    }

    @Override
    public State translateFrom(Map<String, Object> translated) {
        State state = new StateImpl();
        state.setIdentifier((String)translated.get(Properties.IDENTIFICATION));
        state.setUri(URI.create((String)translated.get(Properties.URL)));
        state.setScreenShot(new File((String)translated.get(Properties.SCREEN_SHOT_PATH)));
        state.setIsRoot(translated.get(Properties.IS_ROOT).equals(BOOLEAN_TRUE));
        state.setDocumentFilePath((String)translated.get(Properties.DOCUMENT_FILE));

        if (translated.containsKey(Properties.DOCUMENT_FILE)) {
            File html = new File((String)translated.get(Properties.DOCUMENT_FILE));

            try {
                Document document = Jsoup.parse(html, "UTF-8");
                state.setDocument(document);
            } catch (IOException e) { }
        }

        return state;
    }
}
