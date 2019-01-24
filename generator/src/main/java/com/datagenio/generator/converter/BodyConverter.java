package com.datagenio.generator.converter;

import com.datagenio.crawler.api.RemoteRequest;
import com.datagenio.model.request.AbstractBody;
import com.datagenio.model.request.TypedParamImpl;
import com.datagenio.model.util.ParamTypeMatcher;

public class BodyConverter {

    public AbstractBody process(RemoteRequest remoteRequest) {
        var body = new AbstractBody();

        // TODO: Determine if param is required. Info should be passed via RemoteRequest.
        remoteRequest.getBody().getParts().forEach(part -> {
            body.addProperty(new TypedParamImpl(part.getName(), ParamTypeMatcher.match(part.getContentType())));
        });
        return body;
    }
}
