package dev.cheercode.collector;

import dev.cheercode.contract.FileFilter;
import dev.cheercode.contract.FileItem;
import dev.cheercode.model.FileItemImpl;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class FileCollector {
    private final FileFilter fileFilter;

    public FileCollector(FileFilter fileFilter) {
        this.fileFilter = fileFilter;
    }

    public List<FileItem> collectFiles(String inputPath) throws IOException {
        if (Files.isDirectory(Paths.get(inputPath))) {
            return collectFromDirectory(inputPath);
        } else if (isArchive(inputPath)) {
            return collectFromArchive(inputPath);
        }
        throw new IllegalArgumentException("Unsupported input path type:" + inputPath);
    }

    private List<FileItem> collectFromDirectory(String inputPath) throws IOException {
        Path directory = Paths.get(inputPath);
        Map<String, FileItem> items = new TreeMap<>();
        Set<String> includedDirectories = new HashSet<>();

        Files.walk(directory)
                .filter(Files::isRegularFile)
                .filter(file -> fileFilter.shouldInclude(file.toString()))
                .forEach(file -> {
                    String relativePath = directory.relativize(file).toString().replace('\\', '/');
                    items.put(relativePath, new FileItemImpl(relativePath, false, relativePath));

                    addParentDirectories(relativePath, includedDirectories);
                });

        for (String dirPath : includedDirectories) {
            items.put(dirPath, new FileItemImpl(dirPath, true, dirPath));
        }

        return new ArrayList<>(items.values());
    }

    private List<FileItem> collectFromArchive(String inputPath) throws IOException {
        List<FileItem> items = new ArrayList<>();
        Set<String> includedDirectories = new TreeSet<>();

        try (ZipInputStream zis = new ZipInputStream(
                new FileInputStream(inputPath), StandardCharsets.UTF_8)) {

            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {
                String entryName = entry.getName().replace('\\', '/');

                if (!entry.isDirectory() && fileFilter.shouldInclude(entryName)) {
                    items.add(new FileItemImpl(entryName, false, entryName));
                    addParentDirectories(entryName, includedDirectories);
                }
                zis.closeEntry();
            }
        }

        List<FileItem> result = new ArrayList<>();
        for (String dir : includedDirectories) {
            result.add(new FileItemImpl(dir, true, dir));
        }
        result.addAll(items);

        return result;
    }

    private void addParentDirectories(String filePath, Set<String> directories) {
        String parentPath = filePath;
        while (parentPath.contains("/")) {
            parentPath = parentPath.substring(0, parentPath.lastIndexOf("/"));
            if (!parentPath.isEmpty()) {
                directories.add(parentPath);
            }
        }
    }

    private boolean isArchive(String inputPath) {
        String lowerPath = inputPath.toLowerCase();
        return lowerPath.endsWith(".zip") ||
                lowerPath.endsWith(".jar") ||
                lowerPath.endsWith(".war");
    }
}
