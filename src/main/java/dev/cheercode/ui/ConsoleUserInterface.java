package dev.cheercode.ui;

import dev.cheercode.contract.UserInterface;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;

public class ConsoleUserInterface implements UserInterface {
    private final Scanner scanner;

    // ANSI цветовые коды
    private static final String RESET = "\u001B[39m";
    private static final String BRIGHT_RED = "\u001B[91m";
    private static final String BRIGHT_GREEN = "\u001B[92m";
    private static final String BRIGHT_YELLOW = "\u001B[93m";

    public ConsoleUserInterface() {
        this.scanner = new Scanner(System.in);
    }

    @Override
    public String getInputPath() {
        System.out.println("Введите путь к архиву или директории: ");
        return scanner.nextLine().trim();
    }

//    @Override
//    public String getOutputPath() {
//        System.out.println("Введите имя выходного файла (например, project.txt): ");
//        String path = scanner.nextLine().trim();
//
//        if (path.isEmpty()) {
//            path = "aggregated_files.txt";
//            showMessage("Используется имя по умолчанию: " + path);
//        }
//
//        if (!path.toLowerCase().endsWith(".txt")) {
//            path += ".txt";
//            showMessage("Добавлено расширение .txt: " + path);
//        }
//
//        return path;
//    }

    @Override
    public String getOutputPath(String inputPath) {
        Path input = Paths.get(inputPath).toAbsolutePath();
        Path parentDir = input.getParent();  // Родительская директория (для архивов) или сама директория (если это папка)
        String defaultName = "aggregated_files.txt";

        // Если входной путь — директория, сохраняем в ней
        if (Files.isDirectory(input)) {
            parentDir = input;
        }

        // Предлагаем путь по умолчанию
        Path defaultOutput = parentDir.resolve(defaultName);
        System.out.printf("Введите имя выходного файла (по умолчанию: %s): \n", defaultOutput);
        String userPath = scanner.nextLine().trim();

        // Если пользователь ничего не ввёл, используем путь по умолчанию
        Path finalPath;
        if (userPath.isEmpty()) {
            finalPath = defaultOutput;
        } else {
            // Если введён относительный путь — сохраняем в той же директории
            if (!Paths.get(userPath).isAbsolute()) {
                finalPath = parentDir.resolve(userPath);
            } else {
                finalPath = Paths.get(userPath);
            }
        }

        // Добавляем .txt, если его нет
        if (!finalPath.getFileName().toString().toLowerCase().endsWith(".txt")) {
            finalPath = finalPath.resolveSibling(finalPath.getFileName() + ".txt");
            System.out.println("Добавлено расширение .txt: " + finalPath);
        }

        return finalPath.toString();
    }

    @Override
    public String getCommand() {
        System.out.print("> ");
        return scanner.nextLine().trim();
    }

    @Override
    public void showMessage(String message) {
        System.out.println(message);
    }

    @Override
    public void showError(String error) {
        System.err.println("Ошибка: " + error);
    }

    /**
     * Выводит цветное сообщение в зависимости от статуса включения файла
     */
    public void showFileItem(int index, String status, String type, String path, boolean isIncluded) {
        String color = isIncluded ? BRIGHT_GREEN : BRIGHT_RED;
        // Используем фиксированную ширину 6 символов для типа файла для выравнивания
        String formattedMessage = String.format("%3d. %s %-6s %s%s%s",
                index, status, type, color, path, RESET);
        System.out.println(formattedMessage);
    }

    /**
     * Выводит предупреждающее сообщение желтым цветом
     */
    public void showWarning(String message) {
        System.out.println(BRIGHT_YELLOW + "Предупреждение: " + message + RESET);
    }

    public void close() {
        scanner.close();
    }
}