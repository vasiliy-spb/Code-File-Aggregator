package dev.cheercode;

import java.util.Date;
import java.util.List;

public class TxtOutputFormatter implements OutputFormatter {

    @Override
    public void writeHeader(OutputWriter writer, String sourcePath) {
        writer.println("=".repeat(80));
        writer.println("АГРЕГИРОВАННЫЕ ФАЙЛЫ ПРОЕКТА");
        writer.println("Источник: " + sourcePath);
        writer.println("Создано: " + new Date());
        writer.println("=".repeat(80));
        writer.println();
    }

    @Override
    public void writeFileContent(String filePath, List<String> lines, OutputWriter writer) {
        writeFileHeader(writer, filePath);

        for (int i = 0; i < lines.size(); i++) {
            writer.printf("%4d | %s%n", i + 1, lines.get(i));
        }

        writeFileFooter(writer);
    }

    @Override
    public void writeFooter(OutputWriter writer) {
        writer.println("=".repeat(80));
        writer.println("КОНЕЦ ФАЙЛА");
        writer.println("=".repeat(80));
    }

    private void writeFileHeader(OutputWriter writer, String filePath) {
        writer.println();
        writer.println("/" + "=".repeat(78) + "/");
        writer.println("// ФАЙЛ: " + filePath);
        writer.println("/" + "=".repeat(78) + "/");
    }

    private void writeFileFooter(OutputWriter writer) {
        writer.println();
        writer.println("/" + "-".repeat(78) + "/");
        writer.println();
    }
}