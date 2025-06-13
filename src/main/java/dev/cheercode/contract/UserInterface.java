package dev.cheercode.contract;

public interface UserInterface {
    String getInputPath();

    String getOutputPath(String inputPath);

    String getCommand();

    void showMessage(String message);

    void showError(String error);
}