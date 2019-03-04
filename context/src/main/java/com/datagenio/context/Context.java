package com.datagenio.context;

import com.google.gson.annotations.SerializedName;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.validator.routines.UrlValidator;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

public class Context {

    public static final int REQUEST_TIMEOUT = 300;
    public static final int NO_MAX_DEPTH = 0;
    private static final String HTTP = "http";
    private static final String HTTPS = "https";

    @SerializedName("request_timeout")
    private int requestTimeout;

    @SerializedName("crawl_timeout")
    private int crawlTimeout;

    @SerializedName("crawl_depth")
    private int crawlDepth;

    @SerializedName("transition_weight")
    private int defaultTransitionWeight;

    @SerializedName("url")
    private String rootUrl;

    @SerializedName("output_directory")
    private String outputDirName;

    @SerializedName("output_format")
    private String format;

    @SerializedName("verbose")
    private boolean verbose;

    @SerializedName("save_screen_shots")
    private boolean printScreen;

    @SerializedName("continue_model")
    private boolean continueExistingModel;

    @SerializedName("model_only")
    private boolean modelOnly;

    @SerializedName("data_set_only")
    private boolean dataSetOnly;

    @SerializedName("database_configuration")
    private Configuration configuration;

    @SerializedName("event_inputs")
    private List<EventInput> eventInputs;

    @SerializedName("transition_weights")
    private List<TransitionWeight> transitionWeights;

    public Context() {
        crawlDepth              = NO_MAX_DEPTH;
        crawlTimeout            = 0;
        requestTimeout          = REQUEST_TIMEOUT;
        defaultTransitionWeight = 1;
        verbose                 = false;
        printScreen             = true;
        eventInputs             = new ArrayList<>();
        transitionWeights       = new ArrayList<>();
    }

    public String getRootUrl() throws DatagenioException {
        if (StringUtils.isNotBlank(rootUrl)) return rootUrl;
        throw new DatagenioException("No url configured.");
    }

    public void setRootUrl(String rootUrl) {
        if (urlIsValid(rootUrl)) this.rootUrl = rootUrl;
        else throw new IllegalArgumentException("Root URL is not valid.");
    }

    public URI getRootUri() {
        return URI.create(rootUrl);
    }

    public int getRequestTimeout() {
        return requestTimeout;
    }

    public void setRequestTimeout(int requestTimeout) {
        this.requestTimeout = requestTimeout;
    }

    public int getCrawlTimeout() {
        return crawlTimeout;
    }

    public void setCrawlTimeout(int crawlTimeout) {
        this.crawlTimeout = crawlTimeout;
    }

    public int getCrawlDepth() {
        return crawlDepth;
    }

    public void setCrawlDepth(int crawlDepth) {
        this.crawlDepth = crawlDepth;
    }

    public String getOutputDirName() {
        return outputDirName;
    }

    public void setOutputDirName(String outputDirName) {
        if (outputDirIsValid(outputDirName, !(isContinueExistingModel() || isDataSetOnly()))) this.outputDirName = outputDirName;
        else throw new IllegalArgumentException("Output directory is not valid");
    }

    public boolean isVerbose() {
        return verbose;
    }

    public void setVerbose(boolean verbose) {
        this.verbose = verbose;
    }

    public boolean isPrintScreen() {
        return printScreen;
    }

    public void setPrintScreen(boolean printScreen) {
        this.printScreen = printScreen;
    }

    public Configuration getConfiguration() throws DatagenioException {
        if (configuration == null) {
            configuration = new Configuration();
            configuration.setConnectionMode(Configuration.CONNECTION_MODE_EMBEDDED);
            configuration.setRootUrl(getRootUrl());
            configuration.setOutputDirName(getOutputDirName());
            configuration.setRequestSaveMode(Configuration.REQUEST_SAVE_AS_JSON);
        } else {
            if (configuration.getOutputDirName() == null) configuration.setOutputDirName(getOutputDirName());
            if (configuration.getRootUrl() == null) configuration.setRootUrl(getRootUrl());
        }

        return configuration;
    }

    public void setConfiguration(Configuration configuration) {
        this.configuration = configuration;
        this.configuration.setRootUrl(rootUrl);
        this.configuration.setOutputDirName(outputDirName);
    }

    public boolean isContinueExistingModel() {
        return continueExistingModel;
    }

    public void setContinueExistingModel(boolean continueExistingModel) {
        this.continueExistingModel = continueExistingModel;
    }

    public boolean isDataSetOnly() {
        return dataSetOnly;
    }

    public void setDataSetOnly(boolean dataSetOnly) {
        this.dataSetOnly = dataSetOnly;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public boolean isModelOnly() {
        return modelOnly;
    }

    public void setModelOnly(boolean modelOnly) {
        this.modelOnly = modelOnly;
    }

    public List<EventInput> getEventInputs() {
        return eventInputs;
    }

    public void setEventInputs(List<EventInput> eventInputs) {
        this.eventInputs = eventInputs;
    }

    public List<TransitionWeight> getTransitionWeights() {
        return transitionWeights;
    }

    public void setTransitionWeights(List<TransitionWeight> transitionWeights) {
        this.transitionWeights = transitionWeights;
    }

    public int getDefaultTransitionWeight() {
        return defaultTransitionWeight;
    }

    public void setDefaultTransitionWeight(int defaultTransitionWeight) {
        this.defaultTransitionWeight = defaultTransitionWeight;
    }

    public boolean urlIsValid(String url) {
        String[] schemes = {HTTP, HTTPS};
        var validator = new UrlValidator(schemes);

        return validator.isValid(url);
    }

    public boolean outputDirIsValid(String dir, boolean clear) {
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
