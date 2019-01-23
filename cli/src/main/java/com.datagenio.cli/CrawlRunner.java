package com.datagenio.cli;

import com.datagenio.crawler.CrawlContext;
import com.datagenio.crawler.SimpleCrawler;
import com.datagenio.crawler.api.EventFlowGraph;
import com.datagenio.crawler.browser.BrowserFactory;
import com.datagenio.databank.InputBuilderFactory;
import com.datagenio.generator.Generator;
import com.datagenio.generator.GraphConverterImpl;
import com.datagenio.storage.Neo4JReadAdapter;
import com.datagenio.storage.Neo4JWriteAdapter;
import com.datagenio.storage.api.Configuration;
import com.datagenio.storage.connection.ConnectionResolver;
import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import com.google.gson.GsonBuilder;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.ParseException;
import org.apache.commons.io.FileUtils;
import org.apache.commons.validator.routines.UrlValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

public class CrawlRunner {

    private static final String HTTP = "http";
    private static final String HTTPS = "https";
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

        System.exit(0);
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

        System.out.println("Preparing dependencies...");

        var jsonBuilder = new GsonBuilder();
        jsonBuilder.setPrettyPrinting();
        var gson = jsonBuilder.create();

        var crawlContext = new CrawlContext(url, directory, isVerbose(arguments), true);
        var crawler = new SimpleCrawler(crawlContext, BrowserFactory.drivenByFirefox(), InputBuilderFactory.get());
        var configuration = getStorageConfiguration(arguments);
        var readAdapter = new Neo4JReadAdapter(configuration);
        var writeAdapter = new Neo4JWriteAdapter(configuration, ConnectionResolver.get(configuration), gson);

        // Begin modeling site
        System.out.println("Beginning modeling process...");

        var generator = new Generator(crawler, new GraphConverterImpl(), readAdapter, writeAdapter);
        EventFlowGraph graph = generator.crawlSite();

        System.out.println("Finished crawling site.");
        System.out.println("Found " + graph.getStates().size() + " states, and " + graph.getTransitions().size() + " transitions.");

        System.out.println("Finished modeling site.");
    }

    private static boolean isVerbose(CommandLine arguments) {
        return arguments.hasOption(ArgumentParser.VERBOSE);
    }

    private static Configuration getStorageConfiguration(CommandLine arguments) {
        Map<String, String> settings = new HashMap<>();
        settings.put(Configuration.CONNECTION_MODE, Configuration.CONNECTION_MODE_EMBEDDED);
        settings.put(Configuration.OUTPUT_DIRECTORY_NAME, arguments.getOptionValue(ArgumentParser.OUTPUT));
        settings.put(Configuration.SITE_ROOT_URI, arguments.getOptionValue(ArgumentParser.URL));
        settings.put(Configuration.REQUEST_SAVE_MODE, Configuration.REQUEST_SAVE_AS_JSON);

        return new Configuration(settings);
    }

    public static boolean urlIsValid(String url) {
        String[] schemes = {HTTP, HTTPS};
        var validator = new UrlValidator(schemes);

        return validator.isValid(url);
    }

    public static boolean outputDirIsValid(String dir) {
        if (dir == null) {
            return false;
        }

        File directory = new File(dir);
        if (!directory.exists()) {
            directory.mkdir();
        }

        if (directory.list().length > 0) {
            try {
                FileUtils.deleteDirectory(directory);
                directory.mkdir();
            } catch (IOException e) {
                System.out.println(e.getMessage());
                return false;
            }
        }

        return directory.exists() && directory.isDirectory() && directory.canWrite();
    }
}
