package com.datagenio.generator.util;

import com.datagenio.context.Context;
import com.datagenio.databank.InputBuilderFactory;
import com.datagenio.databank.api.InputBuilder;
import com.datagenio.generator.api.FormattedWriter;
import com.datagenio.generator.api.RequestFormatter;
import com.datagenio.model.WebTransition;
import com.datagenio.model.request.AbstractRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

public abstract class DataSetWriter implements FormattedWriter {

    private static Logger logger = LoggerFactory.getLogger(DataSetWriter.class);
    public static String DATA_SET_DIRECTORY = "datasets";

    private Context context;
    private File outputDirectory;
    private File outputFile;
    private RequestFormatter formatter;
    private InputBuilder inputBuilder;

    public DataSetWriter(Context context) {
        this.context = context;
        this.inputBuilder = InputBuilderFactory.get(context);
        outputDirectory = getValidOutputDirectory(context.getOutputDirName());
    }

    public File getOutputFile() {
        if (outputFile == null) {
            outputFile = new File(outputDirectory, getFileName(formatter));
        }
        return outputFile;
    }

    public RequestFormatter getFormatter() {
        return formatter;
    }

    public void setFormatter(RequestFormatter formatter) {
        this.formatter = formatter;
    }

    public static Logger getLogger() {
        return logger;
    }

    protected String[] getPopulatedRequest(AbstractRequest request) {
        Map<String, String> inputs = inputBuilder.buildInputs(request);
        return formatter.format(request, inputs);
    }

    private File getValidOutputDirectory(String directoryName) {
        File directory = new File(directoryName, DATA_SET_DIRECTORY);
        if (!directory.exists()) {
            directory.mkdir();
        }

        if (!directory.canWrite()) {
            directory.setWritable(true);
        }

        return directory;
    }

    private String getFileName(RequestFormatter formatter)
    {
        SimpleDateFormat dateFormatter = new SimpleDateFormat("dd-MM-yyyy-HH-mm-ss");
        String fileName = String.format(
                "%s-%s.%s",
                context.getRootUri().getHost().toLowerCase().replaceAll("[^a-zA-Z0-9]", ""),
                dateFormatter.format(new Date()),
                formatter.getFormatExtension()
        );
        return  fileName;
    }
}
