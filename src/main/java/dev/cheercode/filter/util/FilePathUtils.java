package dev.cheercode.filter.util;

import java.nio.file.Path;

/**
 * Утилиты для работы с файловыми путями.
 * Ответственность: низкоуровневые операции с путями и именами файлов.
 */
public final class FilePathUtils {

    private FilePathUtils() {
        // Утилитный класс
    }

    /**
     * Извлекает расширение из имени файла
     */
    public static String extractExtension(String fileName) {
        int lastDotIndex = fileName.lastIndexOf('.');
        return lastDotIndex == -1 ? "" : fileName.substring(lastDotIndex);
    }

    /**
     * Проверяет, содержит ли путь указанную директорию в любом месте
     */
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
