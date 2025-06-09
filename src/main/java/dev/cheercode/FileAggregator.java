package dev.cheercode;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class FileAggregator {

    // Расширения файлов, которые будут включены в результат
    private static final Set<String> ALLOWED_EXTENSIONS = new HashSet<>(Arrays.asList(
            ".java", ".js", ".ts", ".py", ".cpp", ".c", ".h", ".hpp", ".cs", ".php",
            ".html", ".css", ".xml", ".json", ".yml", ".yaml", ".properties", ".txt",
            ".md", ".sql", ".sh", ".bat", ".ps1", ".gradle", ".maven", ".pom"
    ));

    // Файлы и папки, которые следует игнорировать
    private static final Set<String> IGNORED_NAMES = new HashSet<>(Arrays.asList(
            ".git", ".idea", ".vscode", "node_modules", "target", "build",
            "bin", "obj", ".gradle", ".settings", "dist", "out"
    ));

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        try {
            System.out.println("=".repeat(60));
            System.out.println("СБОРЩИК ФАЙЛОВ ПРОЕКТА В ОДИН TXT");
            System.out.println("=".repeat(60));
            System.out.println();

            // Запрашиваем путь к источнику
            System.out.println("Введите путь к архиву или директории: ");
            String inputPath = scanner.nextLine().trim();

            if (inputPath.isEmpty()) {
                System.out.println("Ошибка: Путь не может быть пустым!");
                return;
            }

            // Проверяем существование файла/директории
            Path input = Paths.get(inputPath);
            if (!Files.exists(input)) {
                System.out.println("Ошибка: Указанный путь не существует!");
                return;
            }

            // Запрашиваем путь для сохранения результата
            System.out.println("Введите имя выходного файла (например, project.txt): ");
            String outputPath = scanner.nextLine().trim();

            if (outputPath.isEmpty()) {
                outputPath = "aggregated_files.txt";
                System.out.println("Используется имя по умолчанию: " + outputPath);
            }

            // Добавляем расширение .txt если его нет
            if (!outputPath.toLowerCase().endsWith(".txt")) {
                outputPath += ".txt";
                System.out.println("Добавлено расширение .txt: " + outputPath);
            }

            System.out.println();
            System.out.println("Обработка файлов...");

            FileAggregator aggregator = new FileAggregator();
            aggregator.aggregate(inputPath, outputPath);

            System.out.println();
            System.out.println("✓ Файлы успешно собраны в: " + outputPath);

        } catch (Exception e) {
            System.err.println("Ошибка: " + e.getMessage());
            e.printStackTrace();
        } finally {
            scanner.close();
        }
    }

    public void aggregate(String inputPath, String outputPath) throws IOException {
        Path input = Paths.get(inputPath);

        try (PrintWriter writer = new PrintWriter(
                new OutputStreamWriter(
                        new FileOutputStream(outputPath),
                        StandardCharsets.UTF_8
                )
        )) {
            writer.println("=".repeat(80));
            writer.println("АГРЕГИРОВАННЫЕ ФАЙЛЫ ПРОЕКТА");
            writer.println("Источник: " + inputPath);
            writer.println("Создано: " + new Date());
            writer.println("=".repeat(80));
            writer.println();

            if (Files.isDirectory(input)) {
                processDirectory(input, writer);
            } else if (isZipFile(inputPath)) {
                processZipFile(inputPath, writer);
            } else {
                throw new IllegalArgumentException("Путь должен указывать на директорию или ZIP архив");
            }
        }
    }

    private void processDirectory(Path directory, PrintWriter writer) throws IOException {
        Files.walk(directory)
                .filter(Files::isRegularFile)
                .filter(this::shouldIncludeFile)
                .sorted()
                .forEach(file -> processFile(file, directory, writer));
    }

    private void processZipFile(String zipPath, PrintWriter writer) throws IOException {
        try (ZipInputStream zis = new ZipInputStream(
                new FileInputStream(zipPath), StandardCharsets.UTF_8)) {

            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {
                if (!entry.isDirectory() && shouldIncludeZipEntry(entry)) {
                    processZipEntry(entry, zis, writer);
                }
                zis.closeEntry();
            }
        }
    }

    private boolean shouldIncludeFile(Path file) {
        String fileName = file.getFileName().toString();
        String extension = getFileExtension(fileName);

        // Проверяем, что файл не в игнорируемой папке
        for (Path parent = file.getParent(); parent != null; parent = parent.getParent()) {
            if (parent.getFileName() != null && IGNORED_NAMES.contains(parent.getFileName().toString())) {
                return false;
            }
        }

        // Проверяем расширение файла
        return ALLOWED_EXTENSIONS.contains(extension.toLowerCase()) ||
                isConfigFile(fileName) ||
                isDocumentationFile(fileName);
    }

    private boolean shouldIncludeZipEntry(ZipEntry entry) {
        String name = entry.getName();
        String fileName = Paths.get(name).getFileName().toString();
        String extension = getFileExtension(fileName);

        // Проверяем, что файл не в игнорируемой папке
        String[] pathParts = name.split("/");
        for (String part : pathParts) {
            if (IGNORED_NAMES.contains(part)) {
                return false;
            }
        }

        return ALLOWED_EXTENSIONS.contains(extension.toLowerCase()) ||
                isConfigFile(fileName) ||
                isDocumentationFile(fileName);
    }

    private void processFile(Path file, Path basePath, PrintWriter writer) {
        try {
            String relativePath = basePath.relativize(file).toString();
            writeFileHeader(writer, relativePath);

            List<String> lines = Files.readAllLines(file, StandardCharsets.UTF_8);
            for (int i = 0; i < lines.size(); i++) {
                writer.printf("%4d | %s%n", i + 1, lines.get(i));
            }

            writeFileFooter(writer);
        } catch (IOException e) {
            writer.println("// ОШИБКА ЧТЕНИЯ ФАЙЛА: " + e.getMessage());
            writeFileFooter(writer);
        }
    }

    private void processZipEntry(ZipEntry entry, ZipInputStream zis, PrintWriter writer) {
        try {
            writeFileHeader(writer, entry.getName());

            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(zis, StandardCharsets.UTF_8));

            String line;
            int lineNumber = 1;
            while ((line = reader.readLine()) != null) {
                writer.printf("%4d | %s%n", lineNumber++, line);
            }

            writeFileFooter(writer);
        } catch (IOException e) {
            writer.println("// ОШИБКА ЧТЕНИЯ ФАЙЛА ИЗ АРХИВА: " + e.getMessage());
            writeFileFooter(writer);
        }
    }

    private void writeFileHeader(PrintWriter writer, String filePath) {
        writer.println();
        writer.println("/" + "=".repeat(78) + "/");
        writer.println("// ФАЙЛ: " + filePath);
        writer.println("/" + "=".repeat(78) + "/");
    }

    private void writeFileFooter(PrintWriter writer) {
        writer.println();
        writer.println("/" + "-".repeat(78) + "/");
        writer.println();
    }

    private String getFileExtension(String fileName) {
        int lastDot = fileName.lastIndexOf('.');
        return lastDot == -1 ? "" : fileName.substring(lastDot);
    }

    private boolean isConfigFile(String fileName) {
        return fileName.equals("Dockerfile") ||
                fileName.equals("Makefile") ||
                fileName.equals("README") ||
                fileName.equals("LICENSE") ||
                fileName.equals("pom.xml") ||
                fileName.equals("build.gradle") ||
                fileName.equals("package.json") ||
                fileName.equals("requirements.txt") ||
                fileName.equals("Cargo.toml") ||
                fileName.equals("go.mod");
    }

    private boolean isDocumentationFile(String fileName) {
        return fileName.toLowerCase().startsWith("readme") ||
                fileName.toLowerCase().startsWith("changelog") ||
                fileName.toLowerCase().startsWith("license");
    }

    private boolean isZipFile(String path) {
        return path.toLowerCase().endsWith(".zip") ||
                path.toLowerCase().endsWith(".jar") ||
                path.toLowerCase().endsWith(".war");
    }
}
