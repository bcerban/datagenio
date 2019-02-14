package com.datagenio.crawler.util;

import com.datagenio.crawler.exception.PersistenceException;
import net.lightbody.bmp.core.har.Har;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class HtmlSaver {
    public static String HTML_DIRECTORY = "html";
    public static String HTML_SUFFIX = "html";

    public static String saveHtml(String content, String fileName, String outputDirectoryName) throws PersistenceException {
        File outputDirectory = getValidOutputDirectory(outputDirectoryName);
        File htmlFile = new File(outputDirectory, fileName + "." + HTML_SUFFIX);
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(htmlFile));
            writer.write(content);
            writer.close();
            return htmlFile.getAbsolutePath();
        } catch (IOException e) {
            throw new PersistenceException("Can't save html file.", e);
        }
    }

    private static File getValidOutputDirectory(String directoryName) {
        File directory = new File(directoryName, HTML_DIRECTORY);
        if (!directory.exists()) {
            directory.mkdir();
        }

        if (!directory.canWrite()) {
            directory.setWritable(true);
        }

        return directory;
    }
}
