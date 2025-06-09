package dev.cheercode.contract;

public interface FileItem {
    String getPath();

    boolean isDirectory();

    boolean isIncluded();

    void setIncluded(boolean included);

    String getDisplayName();
}
