package com.datagenio.crawler.api;

public interface RemoteRequestBodyPart {

    String getName();
    String getValue();
    String getContentType();


    void setName(String name);
    void setValue(String value);
    void setContentType(String type);
}
