package dev.cheercode.model;

import dev.cheercode.contract.FileItem;

public class FileItemImpl implements FileItem {
    private final String path;
    private final boolean isDirectory;
    private boolean included;
    private final String displayName;

    public FileItemImpl(String path, boolean isDirectory, String displayName) {
        this.path = path;
        this.isDirectory = isDirectory;
        this.displayName = displayName;
        this.included = true; // По умолчанию все файлы включены
    }

    @Override
    public String getPath() {
        return path;
    }

    @Override
    public boolean isDirectory() {
        return isDirectory;
    }

    @Override
    public boolean isIncluded() {
        return included;
    }

    @Override
    public void setIncluded(boolean included) {
        this.included = included;
    }

    @Override
    public String getDisplayName() {
        return displayName;
    }

    @Override
    public String toString() {
        return String.format("%s %s", included ? "+" : "-", displayName);
    }
}
