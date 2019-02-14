package com.datagenio.cli;

import org.apache.commons.cli.*;

public class ArgumentParser {

    public static final String URL = "url";
    public static final String OUTPUT = "output-dir";
    public static final String DEPTH = "depth";
    public static final String TIME = "time";
    public static final String CONTINUE = "continue";
    public static final String MODEL_ONLY = "model-only";
    public static final String DATASET_ONLY = "data-set-only";
    public static final String VERBOSE = "verbose";
    public static final String VERSION = "version";
    public static final String HELP = "help";

    public static CommandLine parse(String[] args) throws ParseException {
        var commandLineParser = new DefaultParser();
        return commandLineParser.parse(options(), args);
    }

    public static Options options() {
        var urlOption = Option.builder(URL)
                .required(false)
                .hasArg(true)
                .desc("Root URL where the crawl process will begin")
                .build();

        var outputDirOption = Option.builder("o")
                .required(false)
                .hasArg()
                .longOpt(OUTPUT)
                .desc("Output directory to store the generated models")
                .build();

        var maxDepthOption = Option.builder("d")
                .required(false)
                .hasArg()
                .longOpt(DEPTH)
                .desc("Maximum crawl depth allowed")
                .build();

        var maxTimeOption = Option.builder("t")
                .required(false)
                .hasArg()
                .longOpt(TIME)
                .desc("Maximum crawl time allowed")
                .build();

        var continueOption = Option.builder("c")
                .required(false)
                .hasArg(false)
                .longOpt(CONTINUE)
                .desc("Continue from previous model")
                .build();

        var modelOnlyOption = Option.builder("M")
                .required(false)
                .hasArg(false)
                .longOpt(MODEL_ONLY)
                .desc("Only build application model, no data set generated")
                .build();

        var dataSetOnlyOption = Option.builder("D")
                .required(false)
                .hasArg(false)
                .longOpt(DATASET_ONLY)
                .desc("Generate data set for an existing model")
                .build();

        var verboseOption = Option.builder("v")
                .required(false)
                .hasArg(false)
                .longOpt(VERBOSE)
                .desc("Verbose")
                .build();

        var versionOption = Option.builder(VERSION)
                .required(false)
                .hasArg(false)
                .desc("Print module version")
                .build();

        var helpOption = Option.builder("h")
                .required(false)
                .hasArg(false)
                .longOpt(HELP)
                .desc("Print help")
                .build();

        Options options = new Options();
        options.addOption(urlOption);
        options.addOption(outputDirOption);
        options.addOption(maxDepthOption);
        options.addOption(maxTimeOption);
        options.addOption(continueOption);
        options.addOption(modelOnlyOption);
        options.addOption(dataSetOnlyOption);
        options.addOption(verboseOption);
        options.addOption(versionOption);
        options.addOption(helpOption);
        return options;
    }
}
