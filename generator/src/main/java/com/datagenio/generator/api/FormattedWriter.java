package com.datagenio.generator.api;

import com.datagenio.model.request.AbstractRequest;

import java.io.File;
import java.util.List;

public interface FormattedWriter {
    File getOutputFile();
    RequestFormatter getFormatter();
    void formatAndWrite(List<AbstractRequest> requests);
    void writeLines(List<String[]> lines);
}
