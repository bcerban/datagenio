package com.datagenio.crawler.util;

import com.datagenio.crawler.exception.PersistenceException;
import org.junit.Before;
import org.junit.Test;
import org.junit.After;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.Assert.*;

public class ScreenShotSaverTest {

    private byte[] imageBytes;
    private String outputDirectoryName = "/tmp";
    private String stateName = "test-state";
    private File createdDirectory;
    private File createdImage;

    @Before
    public void setUp() throws IOException {
        ClassLoader classLoader = getClass().getClassLoader();
        File imageFile = new File(classLoader.getResource("img/yoga.jpg").getFile());
        BufferedImage bufferedImage = ImageIO.read(imageFile);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ImageIO.write(bufferedImage, "jpg", outputStream);
        imageBytes = outputStream.toByteArray();
        outputStream.close();
    }

    @After
    public void tearDown() {
        if (createdImage != null && createdImage.exists()) {
            createdImage.delete();
        }

        if (createdDirectory != null && createdDirectory.exists()) {
            createdDirectory.delete();
        }
    }

    @Test
    public void testSaveScreenShot() throws PersistenceException {
        ScreenShotSaver.saveScreenShot(imageBytes, stateName, outputDirectoryName);

        createdDirectory = new File(outputDirectoryName, ScreenShotSaver.SCREEN_SHOTS_DIRECTORY);
        createdImage = new File(createdDirectory, stateName + ".jpg");

        assertTrue(createdDirectory.exists());
        assertTrue(createdDirectory.canWrite());
        assertTrue(createdImage.exists());
    }
}
