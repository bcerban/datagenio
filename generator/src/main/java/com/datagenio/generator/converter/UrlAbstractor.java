package com.datagenio.generator.converter;

import com.datagenio.model.api.AbstractUrl;
import com.datagenio.model.api.ParamTypes;
import com.datagenio.model.request.AbstractUrlImpl;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;

import java.net.URI;
import java.util.List;

public class UrlAbstractor {

    public AbstractUrl process(URI uri) {
        AbstractUrl abstractUrl = new AbstractUrlImpl();

        // Initial assumption is that URI path does not contain variables.
        // This is known to be untrue for many website, especially those based on frameworks
        // Additional processing is needed to guess which path parts are in fact params
        abstractUrl.setBaseUrl(String.format("%s://%s%s", uri.getScheme(), uri.getHost(), uri.getPath()));

        // We consider URL params required in every case
        List<NameValuePair> params = URLEncodedUtils.parse(uri, "UTF-8");
        params.forEach((param) -> abstractUrl.addTypedParam(param.getName(), ParamTypes.ALPHANUMERIC, true));

        return abstractUrl;
    }
}
