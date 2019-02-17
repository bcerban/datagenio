package com.datagenio.generator.converter;

import com.datagenio.context.EventInput;
import com.datagenio.crawler.api.Eventable;
import com.datagenio.databank.api.InputBuilder;
import com.datagenio.model.request.AbstractUrl;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;

import java.net.URI;
import java.util.List;

public class UrlAbstractor {

    private InputBuilder inputBuilder;

    public UrlAbstractor(InputBuilder inputBuilder) {
        this.inputBuilder = inputBuilder;
    }

    /**
     * Convert an {@link URI} to an {@link AbstractUrl}
     * @param uri the URI to abstract
     * @return a new {@link AbstractUrl}
     */
    public AbstractUrl process(URI uri) {
        AbstractUrl abstractUrl = processUrl(uri);

        // We consider URL params required in every case
        List<NameValuePair> params = URLEncodedUtils.parse(uri, "UTF-8");
        params.forEach((param) -> abstractUrl.addTypedParam(
                param.getName(),
                inputBuilder.getProviderByType(InputBuilder.DEFAULT).getType(),
                true)
        );

        return abstractUrl;
    }

    /**
     * Convert an {@link URI} to an {@link AbstractUrl} matching inputs from a triggered event.
     *
     * @param uri the URI to abstract
     * @param event the triggered {@link Eventable}
     * @param inputs the inputs provided on event execution
     * @return a new {@link AbstractUrl}
     */
    public AbstractUrl process(URI uri, Eventable event, List<EventInput> inputs) {
        AbstractUrl abstractUrl = processUrl(uri);

        // We consider URL params required in every case
        List<NameValuePair> params = URLEncodedUtils.parse(uri, "UTF-8");
        params.forEach(param -> abstractUrl.addTypedParam(
                ParamProcessor.processPart(param.getName(), event.getSource(), inputs, inputBuilder))
        );

        return abstractUrl;
    }

    /**
     * Initial assumption is that URI path does not contain variables.
     * This is known to be untrue for many website, especially those based on frameworks
     * Additional processing is needed to guess which path parts are in fact params
     *
     * @param uri the {@link URI} to abstract
     * @return a new {@link AbstractUrl}
     */
    private AbstractUrl processUrl(URI uri) {
        String baseUrl = String.format("%s://%s%s", uri.getScheme(), uri.getHost(), uri.getPath());
        return new AbstractUrl(baseUrl);
    }
}
