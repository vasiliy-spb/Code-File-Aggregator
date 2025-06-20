package dev.cheercode.contract;

import java.io.IOException;

public interface FileProcessor {
    void processFiles(String inputPath, OutputWriter writer) throws IOException;

    boolean canProcess(String inputPath);
}
