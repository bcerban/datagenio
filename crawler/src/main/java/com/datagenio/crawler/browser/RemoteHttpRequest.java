package com.datagenio.crawler.browser;

import com.datagenio.crawler.api.RemoteRequest;
import net.lightbody.bmp.core.har.HarRequest;

import java.util.HashMap;
import java.util.Map;

public class RemoteHttpRequest implements RemoteRequest {
    private String method;
    private String url;
    private String protocol;
    private String version;
    private String body;
    private Map<String, String> headers;
    private Map<String, String> cookies;
    private int sortOrder;

    public RemoteHttpRequest() {
        headers = new HashMap<>();
        cookies = new HashMap<>();
    }

    public RemoteHttpRequest(HarRequest harRequest) {
        this();

        method = harRequest.getMethod();
        url = harRequest.getUrl();
        version = harRequest.getHttpVersion();

        harRequest.getHeaders().forEach(header -> {
            headers.put(header.getName(), header.getValue());
        });

        harRequest.getCookies().forEach(cookie -> {
            cookies.put(cookie.getName(), cookie.getValue());
        });
    }

    @Override
    public String getMethod() {
        return method;
    }

    @Override
    public String getUrl() {
        return url;
    }

    @Override
    public String getProtocol() {
        return protocol;
    }

    @Override
    public String getVersion() {
        return version;
    }

    @Override
    public String getBody() {
        return body;
    }

    @Override
    public Map<String, String> getHeaders() {
        return headers;
    }

    @Override
    public Map<String, String> getCookies() {
        return cookies;
    }

    @Override
    public int getSortOrder() {
        return sortOrder;
    }

    @Override
    public void setMethod(String method) {
        this.method = method;
    }

    @Override
    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    @Override
    public void setVersion(String version) {
        this.version = version;
    }

    @Override
    public void setBody(String body) {
        this.body = body;
    }

    @Override
    public void setHeaders(Map<String, String> headers) {
        this.headers = headers;
    }

    @Override
    public void setCookies(Map<String, String> cookies) {
        this.cookies = cookies;
    }

    @Override
    public void setSortOrder(int order) {
        this.sortOrder = order;
    }
}
