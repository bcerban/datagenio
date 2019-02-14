package com.datagenio.storage.translator;

import com.datagenio.model.WebState;
import com.datagenio.model.request.*;
import com.datagenio.storageapi.Properties;
import com.datagenio.storageapi.Translator;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

public class WebStateTranslator implements Translator<WebState, Map<String, Object>> {

    private Gson gson;

    public WebStateTranslator() {
        gson = new GsonBuilder()
                .setPrettyPrinting()
                .create();
    }

    @Override
    public Map<String, Object> buildProperties(WebState original) {
        Map<String, Object> properties = new HashMap<>();
        properties.put(Properties.IDENTIFICATION, original.getIdentifier());
        properties.put(Properties.URL, gson.toJson(original.getUrl(), AbstractUrl.class));
        properties.put(Properties.IS_ROOT, original.isRoot() ? BOOLEAN_TRUE : BOOLEAN_FALSE);
        properties.put(Properties.EXTERNAL_IDS, gson.toJson(original.getExternalIds()));
        properties.put(Properties.ABSTRACT_REQUESTS, gson.toJson(
                original.getRequests(), new TypeToken<ArrayList<AbstractRequest>>() { }.getType()
        ));

        var screenShotFiles = original.getScreenShots()
                .stream()
                .map(file -> file.getAbsolutePath()).collect(Collectors.toList());

        properties.put(Properties.SCREEN_SHOTS, gson.toJson(screenShotFiles));
        return properties;
    }

    @Override
    public WebState translateFrom(Map<String, Object> translated) {
        var state = new WebState();
        state.setIdentifier((String)translated.get(Properties.IDENTIFICATION));
        state.setIsRoot(translated.get(Properties.IS_ROOT).equals(BOOLEAN_TRUE));
        state.setUrl(gson.fromJson((String)translated.get(Properties.URL), AbstractUrl.class));

        var externalIds = (String)translated.get(Properties.EXTERNAL_IDS);
        state.setExternalIds(Arrays.asList(gson.fromJson(externalIds, String[].class)));

        var screenShots = Arrays.asList(
                gson.fromJson((String)translated.get(Properties.SCREEN_SHOTS), String[].class)
        );
        Collection<File> screenShotFiles = screenShots.stream().map(shot -> new File(shot)).collect(Collectors.toList());
        state.setScreenShots(screenShotFiles);

        var abstractRequests = (ArrayList<AbstractRequest>)gson.fromJson(
                (String)translated.get(Properties.ABSTRACT_REQUESTS),
                new TypeToken<ArrayList<AbstractRequest>>() { }.getType()
        );
        state.setRequests(abstractRequests);

        return state;
    }
}
