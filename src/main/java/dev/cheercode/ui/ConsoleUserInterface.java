package dev.cheercode.ui;

import dev.cheercode.contract.UserInterface;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;

public class ConsoleUserInterface implements UserInterface {
    private static final String RESET_ANSI_COLOR = "\u001B[39m";
    private static final String BRIGHT_RED_ANSI_COLOR = "\u001B[91m";
    private static final String BRIGHT_GREEN_ANSI_COLOR = "\u001B[92m";
    private static final String BRIGHT_YELLOW_ANSI_COLOR = "\u001B[93m";
    private final Scanner scanner;

    public ConsoleUserInterface() {
        this.scanner = new Scanner(System.in);
    }

    @Override
    public String getInputPath() {
        System.out.println("Введите путь к архиву или директории: ");
        return scanner.nextLine().trim();
    }

    @Override
    public String getOutputPath(String inputPath) {
        Path input = Paths.get(inputPath).toAbsolutePath();
        Path parentDir = input.getParent();
        String defaultName = "aggregated_files.txt";

        if (Files.isDirectory(input)) {
            parentDir = input;
        }

        Path defaultOutput = parentDir.resolve(defaultName);
        System.out.printf("Введите имя выходного файла (по умолчанию: %s): \n", defaultOutput);
        String userPath = scanner.nextLine().trim();

        Path finalPath;
        if (userPath.isEmpty()) {
            finalPath = defaultOutput;
        } else {
            if (!Paths.get(userPath).isAbsolute()) {
                finalPath = parentDir.resolve(userPath);
            } else {
                finalPath = Paths.get(userPath);
            }
        }

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

    public void showFileItem(int index, String status, String type, String path, boolean isIncluded) {
        String color = isIncluded ? BRIGHT_GREEN_ANSI_COLOR : BRIGHT_RED_ANSI_COLOR;
        String formattedMessage = String.format("%3d. %s %-6s %s%s%s",
                index, status, type, color, path, RESET_ANSI_COLOR);
        System.out.println(formattedMessage);
    }

    public void showWarning(String message) {
        System.out.println(BRIGHT_YELLOW_ANSI_COLOR + "Предупреждение: " + message + RESET_ANSI_COLOR);
    }

    public void close() {
        scanner.close();
    }
}