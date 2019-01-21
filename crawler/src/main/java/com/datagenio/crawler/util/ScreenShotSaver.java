package com.datagenio.crawler.util;

import com.datagenio.crawler.exception.PersistenceException;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;

public class ScreenShotSaver {

    public static String SCREEN_SHOTS_DIRECTORY = "screenshots";
    public static String JPG_SUFFIX = "jpg";

    public static File saveScreenShot(byte[] screenShot, String name, String outputDirectoryName) throws PersistenceException {
        File outputDirectory = getValidOutputDirectory(outputDirectoryName);
        File screenShotFile = new File(outputDirectory, name + "." + JPG_SUFFIX);

        try {
            writeScreenShotImage(screenShot, screenShotFile);
            return screenShotFile;
        } catch (IOException e) {
            throw new PersistenceException("Unexpected exception while trying to save image " + screenShotFile.getName(), e);
        }
    }

    private static void writeScreenShotImage(byte[] screenShot, File screenShotFile) throws IOException {
        Image image = ImageIO.read(new ByteArrayInputStream(screenShot));
        BufferedImage bufferedImage = new BufferedImage(
                image.getWidth(null),
                image.getHeight(null),
                BufferedImage.TYPE_INT_RGB
        );
        Graphics2D graphics = bufferedImage.createGraphics();
        graphics.drawImage(image, 0, 0, Color.WHITE, null);
        graphics.dispose();
        ImageIO.write(bufferedImage, JPG_SUFFIX, screenShotFile);
    }

    private static File getValidOutputDirectory(String directoryName) {
        File directory = new File(directoryName, SCREEN_SHOTS_DIRECTORY);
        if (!directory.exists()) {
            directory.mkdir();
        }

        if (!directory.canWrite()) {
            directory.setWritable(true);
        }

        return directory;
    }
}
