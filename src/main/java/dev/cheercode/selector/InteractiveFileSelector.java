package dev.cheercode.selector;

import dev.cheercode.contract.FileFilter;
import dev.cheercode.contract.FileItem;
import dev.cheercode.contract.FileSelector;
import dev.cheercode.contract.UserInterface;
import dev.cheercode.model.FileItemImpl;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.io.FileInputStream;
import java.nio.charset.StandardCharsets;

public class InteractiveFileSelector implements FileSelector {
    private final FileFilter fileFilter;
    private final UserInterface userInterface;

    public InteractiveFileSelector(FileFilter fileFilter, UserInterface userInterface) {
        this.fileFilter = fileFilter;
        this.userInterface = userInterface;
    }

    @Override
    public List<FileItem> collectFiles(String inputPath) {
        try {
            if (Files.isDirectory(Paths.get(inputPath))) {
                return collectFromDirectory(inputPath);
            } else if (isArchive(inputPath)) {
                return collectFromArchive(inputPath);
            }
        } catch (IOException e) {
            userInterface.showError("Ошибка при сборе файлов: " + e.getMessage());
        }
        return new ArrayList<>();
    }

    private List<FileItem> collectFromDirectory(String inputPath) throws IOException {
        Path directory = Paths.get(inputPath);
        Map<String, FileItem> items = new TreeMap<>();

        // Сначала собираем все директории
        Files.walk(directory)
                .filter(Files::isDirectory)
                .filter(path -> !path.equals(directory))
                .forEach(path -> {
                    String relativePath = directory.relativize(path).toString().replace('\\', '/');
                    items.put(relativePath, new FileItemImpl(relativePath, true, relativePath));
                });

        // Затем собираем файлы
        Files.walk(directory)
                .filter(Files::isRegularFile)
                .filter(file -> fileFilter.shouldInclude(file.toString()))
                .forEach(file -> {
                    String relativePath = directory.relativize(file).toString().replace('\\', '/');
                    items.put(relativePath, new FileItemImpl(relativePath, false, relativePath));
                });

        return new ArrayList<>(items.values());
    }

    private List<FileItem> collectFromArchive(String inputPath) throws IOException {
        List<FileItem> items = new ArrayList<>();
        Set<String> directories = new TreeSet<>();

        try (ZipInputStream zis = new ZipInputStream(
                new FileInputStream(inputPath), StandardCharsets.UTF_8)) {

            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {
                String entryName = entry.getName().replace('\\', '/');

                if (entry.isDirectory()) {
                    directories.add(entryName.endsWith("/") ? entryName.substring(0, entryName.length()-1) : entryName);
                } else if (fileFilter.shouldInclude(entryName)) {
                    // Добавляем родительские директории
                    String parentDir = entryName.substring(0, Math.max(0, entryName.lastIndexOf('/')));
                    while (!parentDir.isEmpty() && !directories.contains(parentDir)) {
                        directories.add(parentDir);
                        parentDir = parentDir.substring(0, Math.max(0, parentDir.lastIndexOf('/')));
                    }

                    items.add(new FileItemImpl(entryName, false, entryName));
                }
                zis.closeEntry();
            }
        }

        // Добавляем директории в начало списка
        List<FileItem> result = new ArrayList<>();
        for (String dir : directories) {
            result.add(new FileItemImpl(dir, true, dir));
        }
        result.addAll(items);

        return result;
    }

    private boolean isArchive(String inputPath) {
        String lowerPath = inputPath.toLowerCase();
        return lowerPath.endsWith(".zip") || lowerPath.endsWith(".jar") || lowerPath.endsWith(".war");
    }

    @Override
    public void processUserSelection(List<FileItem> files) {
        if (files.isEmpty()) {
            userInterface.showMessage("Нет файлов для выбора.");
            return;
        }

        userInterface.showMessage("\n" + "=".repeat(60));
        userInterface.showMessage("ВЫБОР ФАЙЛОВ ДЛЯ ВКЛЮЧЕНИЯ В ИТОГОВЫЙ ФАЙЛ");
        userInterface.showMessage("=".repeat(60));
        userInterface.showMessage("Команды:");
        userInterface.showMessage("  -[номер] — исключить файл/директорию");
        userInterface.showMessage("  +[номер] — включить файл/директорию");
        userInterface.showMessage("  +.       — включить все файлы");
        userInterface.showMessage("  -.       — исключить все файлы");
        userInterface.showMessage("  пустая строка — завершить редактирование");
        userInterface.showMessage("=".repeat(60));

        showFileList(files);

        while (true) {
            userInterface.showMessage("\nВведите команду: ");
            String command = userInterface.getCommand();

            if (command.isEmpty()) {
                break;
            }

            if (processCommand(command, files)) {
                showFileList(files);
            }
        }
    }

    private void showFileList(List<FileItem> files) {
        userInterface.showMessage("\nТекущий список файлов:");
        for (int i = 0; i < files.size(); i++) {
            FileItem item = files.get(i);
            String status = item.isIncluded() ? "+" : "-";
            String type = item.isDirectory() ? "[DIR]" : "[FILE]";
            userInterface.showMessage(String.format("%3d. %s %s %s",
                    i + 1, status, type, item.getDisplayName()));
        }
    }

    private boolean processCommand(String command, List<FileItem> files) {
        command = command.trim();

        if (command.equals("+.")) {
            files.forEach(item -> item.setIncluded(true));
            userInterface.showMessage("Все файлы включены.");
            return true;
        }

        if (command.equals("-.")) {
            files.forEach(item -> item.setIncluded(false));
            userInterface.showMessage("Все файлы исключены.");
            return true;
        }

        if (command.startsWith("+") || command.startsWith("-")) {
            boolean include = command.startsWith("+");
            String numberStr = command.substring(1);

            try {
                int number = Integer.parseInt(numberStr);
                if (number >= 1 && number <= files.size()) {
                    FileItem item = files.get(number - 1);

                    if (item.isDirectory()) {
                        // Если это директория, применяем операцию ко всем файлам в ней
                        String dirPath = item.getPath();
                        long affected = files.stream()
                                .filter(f -> f.getPath().startsWith(dirPath + "/") || f.getPath().equals(dirPath))
                                .peek(f -> f.setIncluded(include))
                                .count();

                        userInterface.showMessage(String.format("Директория %s и %d файлов %s.",
                                dirPath, affected - 1, include ? "включены" : "исключены"));
                    } else {
                        item.setIncluded(include);
                        userInterface.showMessage(String.format("Файл %s %s.",
                                item.getPath(), include ? "включен" : "исключен"));
                    }
                    return true;
                } else {
                    userInterface.showError("Номер файла должен быть от 1 до " + files.size());
                }
            } catch (NumberFormatException e) {
                userInterface.showError("Неверный формат номера файла.");
            }
        } else {
            userInterface.showError("Неверная команда. Используйте +[номер], -[номер], +. или -.");
        }

        return false;
    }
}