package dev.cheercode;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class FileAggregator {
    private final UserInterface userInterface;
    private final ProcessorFactory processorFactory;
    private final OutputFormatter formatter;

    public FileAggregator(UserInterface userInterface,
                          ProcessorFactory processorFactory,
                          OutputFormatter formatter) {
        this.userInterface = userInterface;
        this.processorFactory = processorFactory;
        this.formatter = formatter;
    }

    public void run() {
        try {
            showWelcomeMessage();

            String inputPath = getValidInputPath();
            String outputPath = userInterface.getOutputPath();

            userInterface.showMessage("Обработка файлов...");

            aggregateFiles(inputPath, outputPath);

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

    private void aggregateFiles(String inputPath, String outputPath) throws IOException {
        FileProcessor processor = processorFactory.getProcessor(inputPath);

        if (processor == null) {
            throw new IllegalArgumentException("Неподдерживаемый тип файла. Поддерживаются директории и ZIP архивы.");
        }

        try (OutputWriter writer = new FileOutputWriter(outputPath)) {
            formatter.writeHeader(writer, inputPath);
            processor.processFiles(inputPath, writer);
            formatter.writeFooter(writer);
        }
    }
}
