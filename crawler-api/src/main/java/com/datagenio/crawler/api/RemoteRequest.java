package com.datagenio.crawler.api;

import java.util.Map;

public interface RemoteRequest {

    String getMethod();
    String getUrl();
    String getProtocol();
    String getVersion();
    RemoteRequestBody getBody();
    Map<String, String> getHeaders();
    Map<String, String> getCookies();
    int getSortOrder();
    boolean hasBody();

    void setMethod(String method);
    void setUrl(String url);
    void setProtocol(String protocol);
    void setVersion(String version);
    void setBody(RemoteRequestBody body);
    void setHeaders(Map<String, String> headers);
    void setCookies(Map<String, String> cookies);
    void setSortOrder(int order);

}
