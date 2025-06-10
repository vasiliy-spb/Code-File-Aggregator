package dev.cheercode.selector;

import dev.cheercode.collector.FileCollector;
import dev.cheercode.contract.FileFilter;
import dev.cheercode.contract.FileItem;
import dev.cheercode.contract.FileSelector;
import dev.cheercode.contract.UserInterface;
import dev.cheercode.selector.handler.UserSelectionHandler;
import dev.cheercode.selector.presenter.FileListPresenter;

import java.util.*;

public class InteractiveFileSelector implements FileSelector {
    private final FileCollector fileCollector;
    private final UserSelectionHandler selectionHandler;
    private final FileListPresenter listPresenter;
    private final UserInterface userInterface;

    public InteractiveFileSelector(FileFilter fileFilter, UserInterface userInterface) {
        this.userInterface = userInterface;
        this.fileCollector = new FileCollector(fileFilter);
        this.selectionHandler = new UserSelectionHandler(userInterface);
        this.listPresenter = new FileListPresenter(userInterface);
    }

    @Override
    public List<FileItem> collectFiles(String inputPath) {
        try {
            return fileCollector.collectFiles(inputPath);
        } catch (Exception e) {
            userInterface.showError("Ошибка при сборе файлов: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    @Override
    public void processUserSelection(List<FileItem> files) {
        if (files.isEmpty()) {
            userInterface.showMessage("Нет файлов для выбора.");
            return;
        }

        showInstructions();
        listPresenter.showFileList(files);

        while (true) {
            userInterface.showMessage("\nВведите команду: ");
            String command = userInterface.getCommand();

            if (command.isEmpty()) {
                break;
            }

            if (selectionHandler.processCommand(command, files)) {
                listPresenter.showFileList(files);
            }
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
}