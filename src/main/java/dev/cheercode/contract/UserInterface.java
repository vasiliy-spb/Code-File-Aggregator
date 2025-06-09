package dev.cheercode.contract;

public interface UserInterface {
    String getInputPath();

    String getOutputPath();

    void showMessage(String message);

    void showError(String error);
}