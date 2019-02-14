package com.datagenio.model.request;

import com.datagenio.model.Session;

import java.util.*;

public class AbstractRequest {
    private String method;
    private AbstractUrl url;
    private AbstractBody body;
    private Collection<HttpHeader> headers;
    private Session session;
    private int sortOrder;

    public AbstractRequest(String method, AbstractUrl url) {
        headers = new ArrayList<>();
        this.method = method;
        this.url = url;
    }


    public String getMethod() {
        return method;
    }


    public void setMethod(String method) {
        this.method = method;
    }


    public AbstractUrl getUrl() {
        return url;
    }


    public void setUrl(AbstractUrl requestUrl) {
        this.url = requestUrl;
    }


    public AbstractBody getBody() {
        return body;
    }


    public void setBody(AbstractBody requestBody) {
        this.body = requestBody;
    }


    public Collection<HttpHeader> getHeaders() {
        return headers;
    }


    public void setHeaders(Collection<HttpHeader> headers) {
        this.headers = headers;
    }


    public void addHeader(HttpHeader header) {
        this.headers.add(header);
    }


    public void addHeader(String name, String value) {
        this.headers.add(new HttpHeader(name, value));
    }


    public int getSortOrder() {
        return sortOrder;
    }


    public void setSortOrder(int sortOrder) {
        this.sortOrder = sortOrder;
    }

    public Session getSession() {
        return session;
    }

    public void setSession(Session session) {
        this.session = session;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AbstractRequest that = (AbstractRequest) o;
        return Objects.equals(method, that.method) &&
                Objects.equals(url, that.url) &&
                Objects.equals(body, that.body) &&
                Objects.equals(headers, that.headers);
    }

    @Override
    public int hashCode() {
        return Objects.hash(method, url, body, headers);
    }
}
