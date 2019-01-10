package com.datagenio.model.util;

import com.datagenio.model.api.AbstractHttpRequest;
import com.datagenio.model.api.AbstractUrl;
import com.datagenio.model.api.RequestAbstractor;
import com.datagenio.model.request.AbstractRequest;
import com.datagenio.model.request.AbstractUrlImpl;
import org.apache.http.HttpRequest;

public class SimpleRequestAbstractor implements RequestAbstractor {

    @Override
    public AbstractHttpRequest process(HttpRequest request) {
        var abstractRequest = new AbstractRequest(
                request.getRequestLine().getMethod(),
                this.processUrl(request.getRequestLine().getUri())
        );
        return abstractRequest;
    }


    private AbstractUrl processUrl(String url) {
        return new AbstractUrlImpl(url);
    }
}
