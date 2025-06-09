package dev.cheercode.ui;

import dev.cheercode.contract.UserInterface;

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

    @Override
    public String getOutputPath() {
        System.out.println("Введите имя выходного файла (например, project.txt): ");
        String path = scanner.nextLine().trim();

        if (path.isEmpty()) {
            path = "aggregated_files.txt";
            showMessage("Используется имя по умолчанию: " + path);
        }

        if (!path.toLowerCase().endsWith(".txt")) {
            path += ".txt";
            showMessage("Добавлено расширение .txt: " + path);
        }

        return path;
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