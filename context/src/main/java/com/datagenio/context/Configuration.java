package com.datagenio.context;

import com.google.gson.annotations.SerializedName;

public class Configuration {
    public static final String CONNECTION_MODE_EMBEDDED = "embedded";
    public static final String CONNECTION_MODE_REMOTE = "remote";
    public static final String REQUEST_SAVE_AS_NODE = "node";
    public static final String REQUEST_SAVE_AS_JSON = "json";

    @SerializedName("connection_mode")
    private String connectionMode;

    @SerializedName("request_save_mode")
    private String requestSaveMode;

    @SerializedName("host")
    private String host;

    @SerializedName("port")
    private String port;

    @SerializedName("protocol")
    private String protocol;

    @SerializedName("username")
    private String username;

    @SerializedName("password")
    private String password;

    private String outputDirName;
    private String rootUrl;

    public String getConnectionMode() {
        return connectionMode;
    }

    public void setConnectionMode(String connectionMode) {
        this.connectionMode = connectionMode;
    }

    public String getRequestSaveMode() {
        return requestSaveMode;
    }

    public void setRequestSaveMode(String requestSaveMode) {
        this.requestSaveMode = requestSaveMode;
    }

    public String getOutputDirName() {
        return outputDirName;
    }

    public void setOutputDirName(String outputDirName) {
        this.outputDirName = outputDirName;
    }

    public String getRootUrl() {
        return rootUrl;
    }

    public void setRootUrl(String rootUrl) {
        this.rootUrl = rootUrl;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
