package com.datagenio.model.request;

import com.datagenio.model.Session;
import com.datagenio.model.api.AbstractHttpRequest;
import com.datagenio.model.api.AbstractUrl;
import org.apache.http.Header;
import org.apache.http.message.BasicHeader;

import java.util.*;

public class AbstractRequest implements AbstractHttpRequest {
    private String method;
    private AbstractUrl requestUrl;
    private AbstractBody requestBody;
    private Collection<Header> headers;
    private Session session;

    public AbstractRequest(String method, AbstractUrl requestUrl) {
        this.method = method;
        this.requestUrl = requestUrl;
        this.headers = new ArrayList<>();
    }

    @Override
    public String getMethod() {
        return method;
    }

    @Override
    public void setMethod(String method) {
        this.method = method;
    }

    @Override
    public AbstractUrl getUrl() {
        return requestUrl;
    }

    @Override
    public void setUrl(AbstractUrl requestUrl) {
        this.requestUrl = requestUrl;
    }

    @Override
    public AbstractBody getBody() {
        return requestBody;
    }

    @Override
    public void setBody(AbstractBody requestBody) {
        this.requestBody = requestBody;
    }

    @Override
    public Collection<Header> getHeaders() {
        return headers;
    }

    @Override
    public void setHeaders(Collection<Header> headers) {
        this.headers = headers;
    }

    @Override
    public void addHeader(Header header) {
        this.headers.add(header);
    }

    @Override
    public void addHeader(String name, String value) {
        this.headers.add(new BasicHeader(name, value));
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
                Objects.equals(requestUrl, that.requestUrl) &&
                Objects.equals(requestBody, that.requestBody) &&
                Objects.equals(headers, that.headers);
    }

    @Override
    public int hashCode() {
        return Objects.hash(method, requestUrl, requestBody, headers);
    }
}
