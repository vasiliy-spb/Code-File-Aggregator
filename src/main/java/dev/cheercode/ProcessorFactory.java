package dev.cheercode;

import java.util.Arrays;
import java.util.List;

public class ProcessorFactory {
    private final List<FileProcessor> processors;

    public ProcessorFactory(FileFilter fileFilter, OutputFormatter formatter) {
        this.processors = Arrays.asList(
                new DirectoryProcessor(fileFilter, formatter),
                new ZipProcessor(fileFilter, formatter)
        );
    }

    public FileProcessor getProcessor(String inputPath) {
        return processors.stream()
                .filter(processor -> processor.canProcess(inputPath))
                .findFirst()
                .orElse(null);
    }
}