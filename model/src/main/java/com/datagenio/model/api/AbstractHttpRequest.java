package com.datagenio.model.api;

import com.datagenio.model.request.AbstractBody;
import org.apache.http.Header;

import java.util.Collection;

public interface AbstractHttpRequest {

    String getMethod();
    AbstractUrl getUrl();
    AbstractBody getBody();
    Collection<Header> getHeaders();
    int getSortOrder();

    void setMethod(String method);
    void setUrl(AbstractUrl url);
    void setBody(AbstractBody body);
    void setSortOrder(int sortOrder);
    void setHeaders(Collection<Header> headers);

    void addHeader(Header header);
    void addHeader(String name, String value);
}
