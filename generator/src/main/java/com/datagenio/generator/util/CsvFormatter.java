package com.datagenio.generator.util;

import com.datagenio.generator.api.RequestFormatter;
import com.datagenio.model.request.AbstractBody;
import com.datagenio.model.request.AbstractRequest;
import com.datagenio.model.request.AbstractUrl;
import com.datagenio.model.request.HttpHeader;

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;

public class CsvFormatter implements RequestFormatter {

    public static final String CSV_EXTENSION  = "csv";
    public static final String HEADER_METHOD  = "Method";
    public static final String HEADER_URL     = "Url";
    public static final String HEADER_HEADERS = "Headers";
    public static final String HEADER_BODY    = "Body";

    @Override
    public String getFormatExtension() {
        return CSV_EXTENSION;
    }

    @Override
    public String getHeaderLine() {
        return String.format("%s,%s,%s,%s", HEADER_METHOD, HEADER_URL, HEADER_HEADERS, HEADER_BODY);
    }

    @Override
    public boolean requiredHeader() {
        return true;
    }

    @Override
    public String format(AbstractRequest request, Map<String, String> inputs) {
        String[] formattedParts = new String[4];
        formattedParts[0] = request.getMethod();
        formattedParts[1] = formatUrl(request.getUrl(), inputs);
        formattedParts[2] = formatHeaders(request.getHeaders());

        if (request.hasBody()) {
            formattedParts[3] = formatBody(request.getBody(), inputs);
        } else {
            formattedParts[3] = "";
        }

        return Arrays.stream(formattedParts).collect(Collectors.joining(","));
    }

    private String formatUrl(AbstractUrl url, Map<String, String> inputs) {
        if (url.getTypedParams().isEmpty()) return url.getBaseUrl();

        String queryParams = url.getTypedParams().stream()
                .map(p -> String.format("%s=%s", p.getName(), inputs.get(p.getName())))
                .collect(Collectors.joining("&"));

        return String.format("%s?%s", url.getBaseUrl(), queryParams);
    }

    private String formatHeaders(Collection<HttpHeader> headers) {
        String formattedHeaders = headers.stream()
                .map(h -> h.toString())
                .collect(Collectors.joining("\n"));

        return String.format("\"%s\"", formattedHeaders);
    }

    private String formatBody(AbstractBody body, Map<String, String> inputs) {
        if (body.getContentType().equals(AbstractBody.MULTIPART_FORM_DATA)) {
            return formatBodyMultiPart(body, inputs);
        }

        return formatBodyUrlEncoded(body, inputs);
    }

    private String formatBodyUrlEncoded(AbstractBody body, Map<String, String> inputs) {
        String formattedBody = body.getTypedParams().stream()
                .map(p -> String.format("%s=%s", p.getName(), inputs.get(p.getName())))
                .collect(Collectors.joining("&"));
        return String.format("%s", formattedBody);
    }

    private String formatBodyMultiPart(AbstractBody body, Map<String, String> inputs) {
        String formattedBody = body.getTypedParams().stream()
                .map(p -> String.format("%s name=%s\r\n\r\n%s", AbstractBody.FORM_DATA_CONTENT, p.getName(), inputs.get(p.getName())))
                .collect(Collectors.joining("\r\n--" + body.getBoundary() + "\n"));
        return String.format(
                "\"%s %s%s\r\n\r\n--%s\r\n%s\"",
                AbstractBody.MULTIPART_FORM_DATA,
                AbstractBody.FORM_DATA_BOUNDARY,
                body.getBoundary(),
                body.getBoundary(),
                formattedBody
        );
    }
}
