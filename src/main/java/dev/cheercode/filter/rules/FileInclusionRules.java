package dev.cheercode.filter.rules;


import dev.cheercode.filter.config.FileExtensionConfig;
import dev.cheercode.filter.config.SpecialFilesConfig;
import dev.cheercode.filter.util.FilePathUtils;

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Правила определения, должен ли файл быть включен в фильтрацию.
 * Ответственность: применение бизнес-логики фильтрации файлов.
 */
public final class FileInclusionRules {

    private FileInclusionRules() {
        // Утилитный класс
    }

    /**
     * Проверяет, должен ли файл быть включен на основе всех правил
     */
    public static boolean shouldInclude(String filePath) {
        Path path = Paths.get(filePath);
        String fileName = path.getFileName().toString();

        return !isInIgnoredLocation(path) &&
                (isAllowedByExtension(fileName) ||
                        isSpecialFile(fileName));
    }

    /**
     * Проверяет, находится ли файл в игнорируемом месте
     */
    private static boolean isInIgnoredLocation(Path path) {
        for (Path parent = path.getParent(); parent != null; parent = parent.getParent()) {
            Path dirName = parent.getFileName();
            if (dirName != null && SpecialFilesConfig.isIgnoredDirectory(dirName.toString())) {
                return true;
            }
        }
        return false;
    }

    /**
     * Проверяет, разрешен ли файл по расширению
     */
    private static boolean isAllowedByExtension(String fileName) {
        String extension = FilePathUtils.extractExtension(fileName);
        return FileExtensionConfig.isSupported(extension);
    }

    /**
     * Проверяет, является ли файл специальным (конфиг или документация)
     */
    private static boolean isSpecialFile(String fileName) {
        return SpecialFilesConfig.isConfigFile(fileName) ||
                SpecialFilesConfig.hasDocumentationPrefix(fileName);
    }
}
