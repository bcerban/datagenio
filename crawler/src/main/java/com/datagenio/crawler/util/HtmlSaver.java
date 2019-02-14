package com.datagenio.crawler.util;

import com.datagenio.crawler.exception.PersistenceException;
import net.lightbody.bmp.core.har.Har;

import java.io.File;
import java.io.IOException;

public class HarSaver {
    public static String HAR_DIRECTORY = "hars";
    public static String HAR_SUFFIX = "har";

    public static void saveHarFile(Har har, String fileName, String outputDirectoryName) throws PersistenceException {
        File outputDirectory = getValidOutputDirectory(outputDirectoryName);
        File harFile = new File(outputDirectory, fileName + "." + HAR_SUFFIX);
        try {
            har.writeTo(harFile);
        } catch (IOException e) {
            throw new PersistenceException("Can't save har.", e);
        }
    }

    private static File getValidOutputDirectory(String directoryName) {
        File directory = new File(directoryName, HAR_DIRECTORY);
        if (!directory.exists()) {
            directory.mkdir();
        }

        if (!directory.canWrite()) {
            directory.setWritable(true);
        }

        return directory;
    }
}
