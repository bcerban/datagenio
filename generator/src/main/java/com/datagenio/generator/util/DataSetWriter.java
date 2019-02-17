package com.datagenio.generator.util;

import com.datagenio.context.Context;
import com.datagenio.generator.api.RequestFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class DataSetWriter {

    private static Logger logger = LoggerFactory.getLogger(DataSetWriter.class);
    public static String DATA_SET_DIRECTORY = "datasets";

    private Context context;
    private File outputDirectory;
    private File outputFile;

    public DataSetWriter(Context context, RequestFormatter formatter) {
        this.context = context;
        outputDirectory = getValidOutputDirectory(context.getOutputDirName());
        outputFile = new File(outputDirectory, getFileName(formatter));
    }

    public void writeLines(List<String> lines) throws FileNotFoundException {
        FileOutputStream fos = new FileOutputStream(outputFile);
        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));

        lines.forEach(line -> {
            try {
                bw.append(line);
                bw.newLine();
            } catch (IOException e) {
                logger.info("Failed to write line.", e);
            }
        });

        try {
            bw.close();
        } catch (IOException e) {
            logger.info("Error while trying to close file.", e);
        }
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
