package dev.cheercode.filter;

import dev.cheercode.contract.FileFilter;
import dev.cheercode.contract.FileItem;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class SelectiveFileFilter implements FileFilter {
    private final Set<String> includedFiles;
    private final FileFilter baseFilter;

    public SelectiveFileFilter(List<FileItem> selectedFiles, FileFilter baseFilter) {
        this.baseFilter = baseFilter;
        this.includedFiles = selectedFiles.stream()
                .filter(FileItem::isIncluded)
                .filter(item -> !item.isDirectory())
                .map(FileItem::getPath)
                .collect(Collectors.toSet());
    }

    @Override
    public boolean shouldInclude(String filePath) {
        // Нормализуем путь для сравнения
        String normalizedPath = filePath.replace('\\', '/');

        // Сначала проверяем базовый фильтр
        if (!baseFilter.shouldInclude(filePath)) {
            return false;
        }

        // Затем проверяем, включен ли файл пользователем
        return includedFiles.contains(normalizedPath) ||
                includedFiles.stream().anyMatch(included -> normalizedPath.endsWith(included));
    }
}
