package com.datagenio.generator.converter;

import com.datagenio.context.EventInput;
import com.datagenio.crawler.api.Eventable;
import com.datagenio.crawler.api.RemoteRequestBody;
import com.datagenio.databank.api.InputBuilder;
import com.datagenio.model.request.AbstractBody;

import java.util.List;

public class BodyConverter {

    private InputBuilder inputBuilder;

    public BodyConverter(InputBuilder inputBuilder) {
        this.inputBuilder = inputBuilder;
    }

    /**
     * Converts a {@link RemoteRequestBody} into an {@link AbstractBody} by abstracting all its parts.
     *
     * @param remoteRequestBody the {@link RemoteRequestBody} to convert
     * @param event the {@link Eventable} that was triggered to generate this request
     * @param inputs the inputs passed to the event
     * @return a new {@link AbstractBody}
     */
    public AbstractBody process(RemoteRequestBody remoteRequestBody, Eventable event, List<EventInput> inputs) {
        var body = new AbstractBody();
        body.setContentType(remoteRequestBody.getMimeType());
        body.setBoundary(remoteRequestBody.getBoundary());
        remoteRequestBody.getParts().forEach(part -> {
            body.addParam(ParamProcessor.processPart(part, event.getSource(), inputs, inputBuilder));
        });
        return body;
    }
}
