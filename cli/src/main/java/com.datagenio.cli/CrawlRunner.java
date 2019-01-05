package com.datagenio.cli;

import com.datagenio.crawler.Crawler;
import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.ParseException;
import org.apache.commons.validator.routines.UrlValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

public class CrawlRunner {

    private static final int WIDTH = 80;

    private static Logger logger = LoggerFactory.getLogger(CrawlRunner.class);

    public static void main(String[] args) {
        try {
            var arguments = ArgumentParser.parse(args);

            if (arguments.hasOption(ArgumentParser.VERSION)) {
                printVersion();
            } else if (arguments.hasOption(ArgumentParser.HELP)) {
                printUsage();
            } else {
                processUrl(arguments);
            }
        } catch (ParseException e) {
            System.out.println("ERROR: Unreadable input. " + e.getMessage());
        }
    }

    private static void printVersion() {
        String version = "unknown";
        try {
             version = Resources.toString(
                    CrawlRunner.class.getResource("/project.version"), Charsets.UTF_8
            );
        } catch (IOException e) { }

        System.out.println(version);
    }

    private static void printUsage() {
        var helpFormatter = new HelpFormatter();
        var printWriter = new PrintWriter(System.out);

        helpFormatter.printUsage(printWriter, WIDTH, "datagenio", ArgumentParser.options());
        printWriter.flush();
    }

    private static void processUrl(CommandLine arguments) {
        // Validate URL
        String url = arguments.getOptionValue(ArgumentParser.URL);
        if (!urlIsValid(url)) {
            System.out.println("Please provide a valid URL.");
            return;
        }

        // Validate output dir
        String directory = arguments.getOptionValue(ArgumentParser.OUTPUT);
        if (!outputDirIsValid(directory)) {
            System.out.println("Please check directory exists and can be written to.");
            return;
        }

        // Begin modeling site
        System.out.println("Beginning modeling process...");
        // @TODO change this to generator once it is done
        Crawler crawler = new Crawler();


        System.out.println("Finished modeling site.");
    }

    public static boolean urlIsValid(String url) {
        String[] schemes = {"http","https"};
        var validator = new UrlValidator(schemes);

        return validator.isValid(url);
    }

    public static boolean outputDirIsValid(String dir) {
        if (dir == null) {
            return false;
        }

        File directory = new File(dir);
        return directory.exists() && directory.isDirectory() && directory.canWrite();
    }
}
