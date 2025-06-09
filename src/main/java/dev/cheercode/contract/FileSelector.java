package dev.cheercode.contract;

import java.util.List;

public interface FileSelector {
    List<FileItem> collectFiles(String inputPath);

    void processUserSelection(List<FileItem> files);
}
