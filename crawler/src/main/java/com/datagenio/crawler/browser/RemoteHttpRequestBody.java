package com.datagenio.crawler.browser;

import com.datagenio.crawler.api.RemoteRequestBody;
import com.datagenio.crawler.api.RemoteRequestBodyPart;
import net.lightbody.bmp.core.har.HarPostData;
import net.lightbody.bmp.core.har.HarPostDataParam;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class RemoteHttpRequestBody implements RemoteRequestBody {

    public static final String MULTIPART_FORM_DATA = "multipart/form-data;";
    public static final String FORM_DATA_BOUNDARY  = "boundary=";
    public static final String FORM_DATA_CONTENT   = "Content-Disposition: form-data;";

    private String mimeType;
    private String boundary;
    private Collection<RemoteRequestBodyPart> parts;

    public RemoteHttpRequestBody() {
        parts = new ArrayList<>();
    }

    public RemoteHttpRequestBody(HarPostData postData) {
        this();
        mimeType = postData.getMimeType();

        if (postData.getParams() != null) {
            parsePostDataParams(postData.getParams());
        }

        if (postData.getText() != null) {
            parsePostDataText(postData.getText());
        }
    }

    private void parsePostDataParams(List<HarPostDataParam> postDataParams) {
        postDataParams.forEach(param -> addPart(new RemoteHttpRequestBodyPart(param)));
    }

    private void parsePostDataText(String postDataText) {
        String[] mimeTypeParts = mimeType.split(FORM_DATA_BOUNDARY);
        if (mimeTypeParts.length >= 2 && mimeTypeParts[0].trim().toLowerCase().equals(MULTIPART_FORM_DATA)) {
            mimeType = mimeTypeParts[0].trim();
            boundary = mimeTypeParts[1];

            String[] textParts = postDataText.split(boundary);
            if (textParts.length > 0) {
                for (String textPart : textParts) {
                    String[] content = textPart.split(FORM_DATA_CONTENT);
                    if (content.length >= 2) {
                        // Data param should have the form name="{key}"\r\n\r\n{value}\r\n--
                        String postParam = content[1].trim();
                        postParam = postParam.replace("name=", "");
                        String[] keyName = postParam.split("\r\n\r\n");
                        if (keyName.length >= 2) {
                            keyName[1] = keyName[1].replace("\r\n--", "");
                            addPart(new RemoteHttpRequestBodyPart(keyName[0], keyName[1], ""));
                        }
                    }
                }
            }
        }
    }

    @Override
    public String getMimeType() {
        return mimeType;
    }

    @Override
    public Collection<RemoteRequestBodyPart> getParts() {
        return parts;
    }

    @Override
    public void setMimeType(String type) {
        mimeType = type;
    }

    @Override
    public String getBoundary() {
        return boundary;
    }

    @Override
    public void setBoundary(String boundary) {
        this.boundary = boundary;
    }

    @Override
    public void setParts(Collection<RemoteRequestBodyPart> parts) {
        this.parts = parts;
    }

    @Override
    public void addPart(RemoteRequestBodyPart part) {
        parts.add(part);
    }
}
