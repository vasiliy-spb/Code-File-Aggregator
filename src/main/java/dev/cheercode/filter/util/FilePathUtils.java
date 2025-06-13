package dev.cheercode.filter.util;

import java.nio.file.Path;

public final class FilePathUtils {

    private FilePathUtils() {
    }

    public static String extractExtension(String fileName) {
        int lastDotIndex = fileName.lastIndexOf('.');
        return lastDotIndex == -1 ? "" : fileName.substring(lastDotIndex);
    }

    public static boolean containsDirectory(Path filePath, String directoryName) {
        for (Path parent = filePath.getParent(); parent != null; parent = parent.getParent()) {
            Path fileName = parent.getFileName();
            if (fileName != null && fileName.toString().equals(directoryName)) {
                return true;
            }
        }
        return false;
    }
}
