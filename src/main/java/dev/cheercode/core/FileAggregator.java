package dev.cheercode.core;

import dev.cheercode.contract.*;
import dev.cheercode.filter.SelectiveFileFilter;
import dev.cheercode.io.FileOutputWriter;
import dev.cheercode.selector.InteractiveFileSelector;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class FileAggregator {
    private final UserInterface userInterface;
    private final OutputFormatter formatter;
    private final FileFilter baseFileFilter;

    public FileAggregator(UserInterface userInterface,
                          OutputFormatter formatter,
                          FileFilter baseFileFilter) {
        this.userInterface = userInterface;
        this.formatter = formatter;
        this.baseFileFilter = baseFileFilter;
    }

    public void run() {
        try {
            showWelcomeMessage();

            String inputPath = getValidInputPath();
            String outputPath = userInterface.getOutputPath(inputPath);

            FileSelector fileSelector = new InteractiveFileSelector(baseFileFilter, userInterface);
            List<FileItem> availableFiles = fileSelector.collectFiles(inputPath);
            fileSelector.processUserSelection(availableFiles);

            FileFilter selectiveFilter = new SelectiveFileFilter(availableFiles, baseFileFilter);
            ProcessorFactory selectiveProcessorFactory = new ProcessorFactory(selectiveFilter, formatter);

            userInterface.showMessage("Обработка файлов...");

            aggregateFiles(inputPath, outputPath, selectiveProcessorFactory);

            userInterface.showMessage("✓ Файлы успешно собраны в: " + outputPath);

        } catch (Exception e) {
            userInterface.showError(e.getMessage());
            e.printStackTrace();
        }
    }

    private void showWelcomeMessage() {
        userInterface.showMessage("=".repeat(60));
        userInterface.showMessage("СБОРЩИК ФАЙЛОВ ПРОЕКТА В ОДИН TXT");
        userInterface.showMessage("=".repeat(60));
        userInterface.showMessage("");
    }

    private String getValidInputPath() {
        while (true) {
            String inputPath = userInterface.getInputPath();

            if (inputPath.isEmpty()) {
                userInterface.showError("Путь не может быть пустым!");
                continue;
            }

            if (!Files.exists(Paths.get(inputPath))) {
                userInterface.showError("Указанный путь не существует!");
                continue;
            }

            return inputPath;
        }
    }

    private void aggregateFiles(String inputPath, String outputPath, ProcessorFactory processorFactory) throws IOException {
        FileProcessor processor = processorFactory.getProcessor(inputPath);

        if (processor == null) {
            throw new IllegalArgumentException("Unsupported file type. Directories and archives (zip, rar, jar, war) are supported.");
        }

        try (OutputWriter writer = new FileOutputWriter(outputPath)) {
            formatter.writeHeader(writer, inputPath);
            processor.processFiles(inputPath, writer);
            formatter.writeFooter(writer);
        }
    }
}
