package dev.cheercode.selector.presenter;

import dev.cheercode.contract.FileItem;
import dev.cheercode.contract.UserInterface;
import dev.cheercode.ui.ConsoleUserInterface;

import java.util.List;

public class FileListPresenter {
    private final UserInterface userInterface;

    public FileListPresenter(UserInterface userInterface) {
        this.userInterface = userInterface;
    }

    public void showFileList(List<FileItem> files) {
        userInterface.showMessage("\nТекущий список файлов:");

        if (userInterface instanceof ConsoleUserInterface) {
            showColoredFileList(files, (ConsoleUserInterface) userInterface);
        } else {
            showPlainFileList(files);
        }
    }

    private void showColoredFileList(List<FileItem> files, ConsoleUserInterface consoleUI) {
        for (int i = 0; i < files.size(); i++) {
            FileItem item = files.get(i);
            String status = item.isIncluded() ? "+" : "-";
            String type = item.isDirectory() ? "[DIR]" : "[FILE]";
            consoleUI.showFileItem(i + 1, status, type, item.getDisplayName(), item.isIncluded());
        }
    }

    private void showPlainFileList(List<FileItem> files) {
        for (int i = 0; i < files.size(); i++) {
            FileItem item = files.get(i);
            String status = item.isIncluded() ? "+" : "-";
            String type = item.isDirectory() ? "[DIR]" : "[FILE]";
            userInterface.showMessage(String.format("%3d. %s %-6s %s",
                    i + 1, status, type, item.getDisplayName()));
        }
    }
}
