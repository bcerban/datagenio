package com.datagenio.storage.api;

import java.util.Map;

public class Configuration {
    public static final String CONNECTION_MODE = "connection_mode";
    public static final String CONNECTION_MODE_EMBEDDED = "embedded";
    public static final String CONNECTION_MODE_REMOTE = "remote";
    public static final String REMOTE_HOST = "host";
    public static final String REMOTE_PORT = "port";
    public static final String REMOTE_PROTOCOL = "protocol";
    public static final String USERNAME = "username";
    public static final String PASSWORD = "password";
    public static final String OUTPUT_DIRECTORY_NAME = "output_dir_name";
    public static final String SITE_ROOT_URI = "root_uri";

    private Map<String, String> settings;

    public Configuration(Map<String, String> settings) {
        this.settings = settings;
    }

    public String get(String setting) {
        if (settings.containsKey(setting)) {
            return settings.get(setting);
        }

        return "";
    }

    public void set(String setting, String value) {
        settings.put(setting, value);
    }
}
