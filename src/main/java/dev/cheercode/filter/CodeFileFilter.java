package dev.cheercode.filter;

import dev.cheercode.contract.FileFilter;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class CodeFileFilter implements FileFilter {
    private static final Set<String> ALLOWED_EXTENSIONS = new HashSet<>(Arrays.asList(
            ".java", ".js", ".ts", ".py", ".cpp", ".c", ".h", ".hpp", ".cs", ".php",
            ".html", ".css", ".xml", ".json", ".yml", ".yaml", ".properties", ".txt",
            ".md", ".sql", ".sh", ".bat", ".ps1", ".gradle", ".maven", ".pom"
    ));

    private static final Set<String> IGNORED_NAMES = new HashSet<>(Arrays.asList(
            ".git", ".idea", ".vscode", "node_modules", "target", "build",
            "bin", "obj", ".gradle", ".settings", "dist", "out"
    ));

    @Override
    public boolean shouldInclude(String filePath) {
        Path path = Paths.get(filePath);
        String fileName = path.getFileName().toString();
        String extension = getFileExtension(fileName);

        // Проверяем, что файл не в игнорируемой папке
        for (Path parent = path.getParent(); parent != null; parent = parent.getParent()) {
            if (parent.getFileName() != null && IGNORED_NAMES.contains(parent.getFileName().toString())) {
                return false;
            }
        }

        return ALLOWED_EXTENSIONS.contains(extension.toLowerCase()) ||
                isConfigFile(fileName) ||
                isDocumentationFile(fileName);
    }

    private String getFileExtension(String fileName) {
        int lastDot = fileName.lastIndexOf('.');
        return lastDot == -1 ? "" : fileName.substring(lastDot);
    }

    private boolean isConfigFile(String fileName) {
        return fileName.equals("Dockerfile") ||
                fileName.equals("Makefile") ||
                fileName.equals("README") ||
                fileName.equals("LICENSE") ||
                fileName.equals("pom.xml") ||
                fileName.equals("build.gradle") ||
                fileName.equals("package.json") ||
                fileName.equals("requirements.txt") ||
                fileName.equals("Cargo.toml") ||
                fileName.equals("go.mod");
    }

    private boolean isDocumentationFile(String fileName) {
        return fileName.toLowerCase().startsWith("readme") ||
                fileName.toLowerCase().startsWith("changelog") ||
                fileName.toLowerCase().startsWith("license");
    }
}