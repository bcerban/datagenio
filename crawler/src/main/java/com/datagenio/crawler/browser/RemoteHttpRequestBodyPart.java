package com.datagenio.crawler.browser;

import com.datagenio.crawler.api.RemoteRequestBodyPart;
import net.lightbody.bmp.core.har.HarPostDataParam;

public class RemoteHttpRequestBodyPart implements RemoteRequestBodyPart {

    private String name;
    private String value;
    private String contentType;

    public RemoteHttpRequestBodyPart() { }

    public RemoteHttpRequestBodyPart(HarPostDataParam param) {
        name = param.getName();
        value = param.getValue();
        contentType = param.getContentType();
    }

    public RemoteHttpRequestBodyPart(String name, String value, String contentType) {
        this.name = name;
        this.value = value;
        this.contentType = contentType;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getValue() {
        return value;
    }

    @Override
    public String getContentType() {
        return contentType;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public void setContentType(String type) {
        contentType = type;
    }
}
