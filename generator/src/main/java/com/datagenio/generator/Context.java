package com.datagenio.generator;

public class Context {
    private final String outputDirName;
    private final boolean verbose;

    public Context(String outputDirName) {
        this.outputDirName = outputDirName;
        this.verbose = false;
    }

    public Context(String outputDirName, boolean verbose) {
        this.outputDirName = outputDirName;
        this.verbose = verbose;
    }

    public String getOutputDirName() {
        return outputDirName;
    }

    public boolean isVerbose() {
        return verbose;
    }
}
