package dev.cheercode;

public interface FileFilter {
    boolean shouldInclude(String filePath);
}
