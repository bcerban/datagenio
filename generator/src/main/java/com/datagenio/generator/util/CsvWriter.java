package com.datagenio.generator.util;

import com.datagenio.context.Context;
import com.datagenio.model.request.AbstractRequest;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

import java.io.*;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;


public class CsvWriter extends DataSetWriter {

    public CsvWriter(Context context) {
        super(context);
        setFormatter(new CsvFormatter());
    }

    public void formatAndWrite(List<AbstractRequest> requests) {
        List<String[]> lines = new ArrayList<>();
        requests.forEach(request -> lines.add(getPopulatedRequest(request)));
        writeLines(lines);
    }

    public void writeLines(List<String[]> lines) {
        try {
            BufferedWriter bw = Files.newBufferedWriter(getOutputFile().toPath());
            CSVPrinter printer = new CSVPrinter(bw, CSVFormat.DEFAULT.withHeader(getFormatter().getHeaderLine()));

            lines.forEach(line -> {
                try {
                    printer.printRecord(line);
                } catch (IOException e) {
                    getLogger().info("Failed to add write record.", e);
                }
            });

            printer.flush();
            bw.close();
        } catch (IOException e) {
            getLogger().info("Error while trying to close file.", e);
        }
    }
}
