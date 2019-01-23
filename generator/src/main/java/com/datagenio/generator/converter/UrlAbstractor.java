package com.datagenio.generator.converter;

import com.datagenio.model.api.AbstractUrl;
import com.datagenio.model.request.AbstractUrlImpl;

import java.net.URI;

public class UrlAbstractor {

    public AbstractUrl process(URI uri) {
        // TODO: abstract all params as TypedParam
        AbstractUrl abstractUrl = new AbstractUrlImpl();
        abstractUrl.setBaseUrl(String.format("%s://%s", uri.getScheme(), uri.getHost()));
        return abstractUrl;
    }
}
