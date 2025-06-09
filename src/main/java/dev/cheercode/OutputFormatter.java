package dev.cheercode;

import java.util.List;

public interface OutputFormatter {
    void writeHeader(OutputWriter writer, String sourcePath);

    void writeFileContent(String filePath, List<String> lines, OutputWriter writer);

    void writeFooter(OutputWriter writer);
}