package com.datagenio.model;

import java.util.HashMap;
import java.util.Map;

public class AbstractRequest {
    private String method;
    private AbstractUrl requestUrl;
    private AbstractBody requestBody;
    private Map<String, String> headers;
    private Session session;

    public AbstractRequest(String method, AbstractUrl requestUrl) {
        this.method = method;
        this.requestUrl = requestUrl;
        this.headers = new HashMap<>();
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public AbstractUrl getRequestUrl() {
        return requestUrl;
    }

    public void setRequestUrl(AbstractUrl requestUrl) {
        this.requestUrl = requestUrl;
    }

    public AbstractBody getRequestBody() {
        return requestBody;
    }

    public void setRequestBody(AbstractBody requestBody) {
        this.requestBody = requestBody;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public void setHeaders(Map<String, String> headers) {
        this.headers = headers;
    }

    public void addHeader(String header, String value) {
        this.headers.put(header, value);
    }

    public Session getSession() {
        return session;
    }

    public void setSession(Session session) {
        this.session = session;
    }
}
