package com.datagenio.cli;

import com.datagenio.crawler.PersistentCrawler;
import com.datagenio.context.Context;
import com.datagenio.crawler.browser.BrowserFactory;
import com.datagenio.databank.InputBuilderFactory;
import com.datagenio.generator.GeneratorImpl;
import com.datagenio.generator.api.Generator;
import com.datagenio.generator.converter.GraphConverterImpl;
import com.datagenio.generator.converter.BodyConverter;
import com.datagenio.generator.converter.HttpRequestAbstractor;
import com.datagenio.generator.converter.StateConverter;
import com.datagenio.generator.converter.UrlAbstractor;
import com.datagenio.model.api.WebFlowGraph;
import com.datagenio.storage.Neo4JReadAdapter;
import com.datagenio.storage.Neo4JWriteAdapter;
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
        if (!outputDirIsValid(directory, !continuePrevious(arguments))) {
            System.out.println("Please check directory exists and can be written to.");
            return;
        }

        System.out.println("Preparing dependencies...");
        Generator generator = getGenerator(arguments);

        // Begin modeling site
        System.out.println("Beginning modeling process...");
        WebFlowGraph model = generator.generateWebModel();

        System.out.println("Finished modeling site.");
        System.out.println("Found " + model.getStates().size() + " states, and " + model.getTransitions().size() + " transitions.");

        System.out.println("Generating data set...");
        generator.generateDataset(model);
        System.out.println("Finished writing data set.");
    }

    private static GeneratorImpl getGenerator(CommandLine arguments) {
        var jsonBuilder = new GsonBuilder().setPrettyPrinting();
        var gson = jsonBuilder.create();

        var context = getContext(arguments);
        var connection = ConnectionResolver.get(context.getConfiguration());
        var readAdapter = new Neo4JReadAdapter(context.getConfiguration(), connection);
        var writeAdapter = new Neo4JWriteAdapter(context.getConfiguration(), connection, gson);
        var urlAbstractor = new UrlAbstractor();
        var bodyConverter = new BodyConverter();
        var requestAbstractor = new HttpRequestAbstractor(urlAbstractor, bodyConverter);
        var stateConverter = new StateConverter(urlAbstractor, requestAbstractor, context.getRootUri());

        context.setReadAdapter(readAdapter);
        context.setWriteAdapter(writeAdapter);

        var crawler = new PersistentCrawler(context, BrowserFactory.drivenByFirefox(), InputBuilderFactory.get());
        return new GeneratorImpl(context, crawler, new GraphConverterImpl(context, stateConverter, requestAbstractor), readAdapter, writeAdapter);
    }

    private static boolean isVerbose(CommandLine arguments) {
        return arguments.hasOption(ArgumentParser.VERBOSE);
    }

    private static boolean continuePrevious(CommandLine arguments) {
        return arguments.hasOption(ArgumentParser.CONTINUE);
    }

    private static int getMaxDepth(CommandLine arguments) {
        String depth = arguments.getOptionValue(ArgumentParser.DEPTH);
        try {
            return Integer.parseInt(depth);
        } catch (NumberFormatException e) {
            return Context.NO_MAX_DEPTH;
        }
    }

    private static Context getContext(CommandLine arguments) {
        Context context = new Context(
                arguments.getOptionValue(ArgumentParser.URL),
                arguments.getOptionValue(ArgumentParser.OUTPUT),
                isVerbose(arguments),
                true,
                getMaxDepth(arguments)
        );

        context.setContinueExistingModel(continuePrevious(arguments));

        System.out.println("Max exploration depth: " + context.getCrawlDepth());
        return context;
    }

    public static boolean urlIsValid(String url) {
        String[] schemes = {HTTP, HTTPS};
        var validator = new UrlValidator(schemes);

        return validator.isValid(url);
    }

    public static boolean outputDirIsValid(String dir, boolean clear) {
        if (dir == null) {
            return false;
        }

        File directory = new File(dir);
        if (!directory.exists()) {
            directory.mkdir();
        }

        if (clear && directory.list().length > 0) {
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
