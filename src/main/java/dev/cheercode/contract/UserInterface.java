package dev.cheercode.contract;

public interface UserInterface {
    String getInputPath();

    String getOutputPath();

    String getCommand();

    void showMessage(String message);

    void showError(String error);
}