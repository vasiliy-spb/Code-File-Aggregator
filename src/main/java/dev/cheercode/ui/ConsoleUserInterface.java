package dev.cheercode.ui;

import dev.cheercode.contract.UserInterface;

import java.util.Scanner;

public class ConsoleUserInterface implements UserInterface {
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

    public void close() {
        scanner.close();
    }
}