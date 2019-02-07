package com.datagenio.generator.util;

import com.datagenio.context.Context;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.List;

public class DataSetWriter {

    private static Logger logger = LoggerFactory.getLogger(DataSetWriter.class);
    public static String DATA_SET_DIRECTORY = "datasets";

    private Context context;
    private File outputDirectory;
    private File outputFile;

    public DataSetWriter(Context context) {
        this.context = context;
        outputDirectory = getValidOutputDirectory(context.getOutputDirName());
        outputFile = new File(outputDirectory, getFileName());
    }

    public void writeLines(List<String> lines) throws FileNotFoundException {
        FileOutputStream fos = new FileOutputStream(outputFile);
        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));

        lines.forEach(line -> {
            try {
                bw.write(line);
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

    private String getFileName()
    {
        return context.getRootUri().getHost().toLowerCase()
                .replaceAll("[^a-zA-Z0-9]", "") + ".csv";
    }
}