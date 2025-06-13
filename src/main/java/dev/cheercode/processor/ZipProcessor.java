package dev.cheercode.processor;

import dev.cheercode.contract.FileFilter;
import dev.cheercode.contract.FileProcessor;
import dev.cheercode.contract.OutputFormatter;
import dev.cheercode.contract.OutputWriter;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class ZipProcessor implements FileProcessor {
    private final FileFilter fileFilter;
    private final OutputFormatter formatter;

    public ZipProcessor(FileFilter fileFilter, OutputFormatter formatter) {
        this.fileFilter = fileFilter;
        this.formatter = formatter;
    }

    @Override
    public boolean canProcess(String inputPath) {
        String lowerPath = inputPath.toLowerCase();
        return lowerPath.endsWith(".zip") ||
                lowerPath.endsWith(".rar") ||
                lowerPath.endsWith(".jar") ||
                lowerPath.endsWith(".war");
    }

    @Override
    public void processFiles(String inputPath, OutputWriter writer) throws IOException {
        try (ZipInputStream zis = new ZipInputStream(
                new FileInputStream(inputPath), StandardCharsets.UTF_8)) {

            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {
                if (!entry.isDirectory() && fileFilter.shouldInclude(entry.getName())) {
                    processZipEntry(entry, zis, writer);
                }
                zis.closeEntry();
            }
        }
    }

    private void processZipEntry(ZipEntry entry, ZipInputStream zis, OutputWriter writer) {
        try {
            List<String> lines = new ArrayList<>();
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(zis, StandardCharsets.UTF_8));

            String line;
            while ((line = reader.readLine()) != null) {
                lines.add(line);
            }

            formatter.writeFileContent(entry.getName(), lines, writer);
        } catch (IOException e) {
            List<String> errorLines = List.of("// ОШИБКА ЧТЕНИЯ ФАЙЛА ИЗ АРХИВА: " + e.getMessage());
            formatter.writeFileContent(entry.getName(), errorLines, writer);
        }
    }
}