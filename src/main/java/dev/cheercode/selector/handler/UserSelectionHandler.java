package dev.cheercode.selector.handler;

import dev.cheercode.contract.FileItem;
import dev.cheercode.contract.UserInterface;

import java.util.List;

public class UserSelectionHandler {
    private final UserInterface userInterface;

    public UserSelectionHandler(UserInterface userInterface) {
        this.userInterface = userInterface;
    }

    public void processUserSelection(List<FileItem> files) {
        if (files.isEmpty()) {
            userInterface.showMessage("Нет файлов для выбора.");
            return;
        }

        showInstructions();

        while (true) {
            userInterface.showMessage("\nВведите команду: ");
            String command = userInterface.getCommand();

            if (command.isEmpty()) {
                break;
            }

            processCommand(command, files);
        }
    }

    private void showInstructions() {
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
    }

    public boolean processCommand(String command, List<FileItem> files) {
        command = command.trim();

        if (command.equals("+.")) {
            return handleIncludeAll(files);
        }

        if (command.equals("-.")) {
            return handleExcludeAll(files);
        }

        if (command.startsWith("+") || command.startsWith("-")) {
            return handleToggleItem(command, files);
        }

        userInterface.showError("Неверная команда. Используйте +[номер], -[номер], +. или -.");
        return false;
    }

    private boolean handleIncludeAll(List<FileItem> files) {
        files.forEach(item -> item.setIncluded(true));
        userInterface.showMessage("Все файлы включены.");
        return true;
    }

    private boolean handleExcludeAll(List<FileItem> files) {
        files.forEach(item -> item.setIncluded(false));
        userInterface.showMessage("Все файлы исключены.");
        return true;
    }

    private boolean handleToggleItem(String command, List<FileItem> files) {
        boolean include = command.startsWith("+");
        String numberStr = command.substring(1);

        try {
            int number = Integer.parseInt(numberStr);
            if (number >= 1 && number <= files.size()) {
                FileItem item = files.get(number - 1);

                if (item.isDirectory()) {
                    return handleDirectoryToggle(item, files, include);
                } else {
                    return handleFileToggle(item, include);
                }
            } else {
                userInterface.showError("Номер файла должен быть от 1 до " + files.size());
            }
        } catch (NumberFormatException e) {
            userInterface.showError("Неверный формат номера файла.");
        }

        return false;
    }

    private boolean handleDirectoryToggle(FileItem directory, List<FileItem> files, boolean include) {
        String dirPath = directory.getPath();
        long affected = files.stream()
                .filter(f -> f.getPath().startsWith(dirPath + "/") || f.getPath().equals(dirPath))
                .peek(f -> f.setIncluded(include))
                .count();

        userInterface.showMessage(String.format("Директория %s и %d файлов %s.",
                dirPath, affected - 1, include ? "включены" : "исключены"));
        return true;
    }

    private boolean handleFileToggle(FileItem file, boolean include) {
        file.setIncluded(include);
        userInterface.showMessage(String.format("Файл %s %s.",
                file.getPath(), include ? "включен" : "исключен"));
        return true;
    }
}
