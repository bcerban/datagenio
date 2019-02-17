package com.datagenio.cli;

import com.datagenio.context.DatagenioException;
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
import com.datagenio.model.WebFlowGraph;
import com.datagenio.storage.Neo4JReadAdapter;
import com.datagenio.storage.Neo4JWriteAdapter;
import com.datagenio.storage.connection.ConnectionResolver;
import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.ParseException;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;

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

        try {
            Context context = getContext(arguments);
            Generator generator = getGenerator(context);

            if (context.isModelOnly()) {
                modelOnly(generator);
            } else if (context.isDataSetOnly()) {
                dataSetOnly(generator);
            } else {
                modelAndGenerate(generator);
            }
        } catch (DatagenioException e) {
            System.out.println(e.getMessage());
        }
    }

    private static void modelOnly(Generator generator) {
        // Begin modeling site
        System.out.println("Beginning modeling process...");
        WebFlowGraph model = generator.buildWebModel();

        System.out.println("Finished modeling site.");
        System.out.println("Found " + model.getStates().size() + " states, and " + model.getTransitions().size() + " transitions.");
    }

    private static void dataSetOnly(Generator generator) {
        System.out.println("Loading application model...");
        WebFlowGraph model = generator.loadWebModel();

        System.out.println("Generating data set...");
        generator.generateDataset(model);
        System.out.println("Finished writing data set.");
    }

    private static void modelAndGenerate(Generator generator) {
        // Begin modeling site
        System.out.println("Beginning modeling process...");
        WebFlowGraph model = generator.buildWebModel();

        System.out.println("Finished modeling site.");
        System.out.println("Found " + model.getStates().size() + " states, and " + model.getTransitions().size() + " transitions.");

        System.out.println("Generating data set...");
        generator.generateDataset(model);
        System.out.println("Finished writing data set.");
    }

    private static GeneratorImpl getGenerator(Context context) throws DatagenioException {
        var gson = new GsonBuilder().setPrettyPrinting().create();

        var connection          = ConnectionResolver.get(context.getConfiguration());
        var readAdapter         = new Neo4JReadAdapter(context.getConfiguration(), connection);
        var writeAdapter        = new Neo4JWriteAdapter(context.getConfiguration(), connection, gson);
        var urlAbstractor       = new UrlAbstractor(InputBuilderFactory.get(context));
        var bodyConverter       = new BodyConverter(InputBuilderFactory.get(context));
        var requestAbstractor   = new HttpRequestAbstractor(urlAbstractor, bodyConverter);
        var stateConverter      = new StateConverter(urlAbstractor, requestAbstractor, context.getRootUri());

        var crawler = new PersistentCrawler(context, BrowserFactory.drivenByFirefox(), InputBuilderFactory.get(context), readAdapter);
        return new GeneratorImpl(context, crawler, new GraphConverterImpl(context, stateConverter, requestAbstractor), readAdapter, writeAdapter);
    }

    private static boolean isVerbose(CommandLine arguments) {
        return arguments.hasOption(ArgumentParser.VERBOSE);
    }

    private static boolean continuePrevious(CommandLine arguments) {
        return arguments.hasOption(ArgumentParser.CONTINUE);
    }

    private static boolean isModelOnly(CommandLine arguments) {
        return arguments.hasOption(ArgumentParser.MODEL_ONLY);
    }

    private static boolean isDataSetOnly(CommandLine arguments) {
        return arguments.hasOption(ArgumentParser.DATASET_ONLY);
    }

    private static int getMaxDepth(CommandLine arguments) {
        String depth = arguments.getOptionValue(ArgumentParser.DEPTH);
        try {
            return Integer.parseInt(depth);
        } catch (NumberFormatException e) {
            return Context.NO_MAX_DEPTH;
        }
    }

    private static Context getContext(CommandLine arguments) throws DatagenioException {
        if (StringUtils.isNotBlank(arguments.getOptionValue(ArgumentParser.CONFIG))) {
            return getContextFromFile(arguments.getOptionValue(ArgumentParser.CONFIG));
        }

        Context context = new Context();
        context.setRootUrl(arguments.getOptionValue(ArgumentParser.URL));
        context.setOutputDirName(arguments.getOptionValue(ArgumentParser.OUTPUT));
        context.setVerbose(isVerbose(arguments));
        context.setPrintScreen(true);
        context.setCrawlDepth(getMaxDepth(arguments));
        context.setContinueExistingModel((continuePrevious(arguments) || isDataSetOnly(arguments)));
        context.setFormat("csv");
        context.setModelOnly(isModelOnly(arguments));
        return context;
    }

    private static Context getContextFromFile(String path) throws DatagenioException {
        try {
            Gson gson = new Gson();
            BufferedReader bufferedReader = new BufferedReader(new FileReader(path));
            Context context = gson.fromJson(bufferedReader, Context.class);

            if (context != null) {

                // TODO: this should be moved - context should self-validate
                if (!context.outputDirIsValid(
                        context.getOutputDirName(),
                        !(context.isContinueExistingModel() || context.isDataSetOnly())
                )) {
                    throw new DatagenioException("Output directory is not valid.");
                }

                if (!context.urlIsValid(context.getRootUrl())) {
                    throw new DatagenioException("Root URL is not valid.");
                }

                return context;
            }

            throw new DatagenioException("Configuration file not parsed.");
        } catch (FileNotFoundException e) {
            throw new DatagenioException("Configuration file not found.", e);
        }
    }
}
