package dev.cheercode.filter.config;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Конфигурация специальных файлов и директорий.
 * Ответственность: управление списками специальных файлов и игнорируемых директорий.
 */
public final class SpecialFilesConfig {

    private static final Set<String> CONFIG_FILES = createSet(
            "Dockerfile", "Makefile", "CMakeLists.txt", "meson.build",
            "pom.xml", "build.gradle", "build.sbt", "package.json",
            "requirements.txt", "Cargo.toml", "go.mod", "go.sum",
            "Pipfile", "poetry.lock", "yarn.lock", "package-lock.json",
            "composer.json", "composer.lock", "Gemfile", "Gemfile.lock",
            "mix.exs", "Project.toml", "pyproject.toml", "deno.json",
            "bun.lockb", "pubspec.yaml", "BUILD", "WORKSPACE",
            "flake.nix", "shell.nix", "default.nix"
    );

    private static final Set<String> IGNORED_DIRECTORIES = createSet(
            ".git", ".svn", ".hg", ".idea", ".vscode", ".settings",
            "node_modules", "vendor", "target", "build", "bin",
            "obj", "dist", "out", ".gradle", ".maven"
    );

    private static final Set<String> DOCUMENTATION_PREFIXES = createSet(
            "readme", "changelog", "license"
    );

    private SpecialFilesConfig() {
        // Утилитный класс
    }

    public static boolean isConfigFile(String fileName) {
        return CONFIG_FILES.contains(fileName);
    }

    public static boolean isIgnoredDirectory(String directoryName) {
        return IGNORED_DIRECTORIES.contains(directoryName);
    }

    public static boolean hasDocumentationPrefix(String fileName) {
        String lowerCase = fileName.toLowerCase();
        return DOCUMENTATION_PREFIXES.stream()
                .anyMatch(lowerCase::startsWith);
    }

    public static Set<String> getIgnoredDirectories() {
        return IGNORED_DIRECTORIES;
    }

    private static Set<String> createSet(String... items) {
        return Collections.unmodifiableSet(new HashSet<>(Arrays.asList(items)));
    }
}
