package dev.cheercode.processor;

import dev.cheercode.contract.FileFilter;
import dev.cheercode.contract.FileProcessor;
import dev.cheercode.contract.OutputFormatter;
import dev.cheercode.contract.OutputWriter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

public class DirectoryProcessor implements FileProcessor {
    private final FileFilter fileFilter;
    private final OutputFormatter formatter;

    public DirectoryProcessor(FileFilter fileFilter, OutputFormatter formatter) {
        this.fileFilter = fileFilter;
        this.formatter = formatter;
    }

    @Override
    public boolean canProcess(String inputPath) {
        return Files.isDirectory(Paths.get(inputPath));
    }

    @Override
    public void processFiles(String inputPath, OutputWriter writer) throws IOException {
        Path directory = Paths.get(inputPath);

        Files.walk(directory)
                .filter(Files::isRegularFile)
                .filter(file -> fileFilter.shouldInclude(file.toString()))
                .sorted()
                .forEach(file -> processFile(file, directory, writer));
    }

    private void processFile(Path file, Path basePath, OutputWriter writer) {
        try {
            String relativePath = basePath.relativize(file).toString();
            List<String> lines = Files.readAllLines(file, StandardCharsets.UTF_8);
            formatter.writeFileContent(relativePath, lines, writer);
        } catch (IOException e) {
            List<String> errorLines = Arrays.asList("// ОШИБКА ЧТЕНИЯ ФАЙЛА: " + e.getMessage());
            formatter.writeFileContent(file.toString(), errorLines, writer);
        }
    }
}