package dev.cheercode.filter.config;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public final class FileExtensionConfig {

    private static final Set<String> PROGRAMMING_LANGUAGES = createSet(
            // Основные языки
            ".java", ".js", ".ts", ".py", ".cpp", ".c", ".h", ".hpp",
            ".cs", ".php", ".go", ".rs", ".swift", ".rb",

            // JVM языки
            ".kt", ".kts", ".scala", ".clj", ".cljs",

            // Функциональные языки
            ".fs", ".fsx", ".elm", ".hs", ".ml", ".mli",

            // Системные языки
            ".zig", ".v", ".d", ".nim",

            // Скриптовые языки
            ".pl", ".pm", ".r", ".lua", ".jl",

            // Mobile/Desktop
            ".m", ".mm", ".dart", ".vb",

            // Другие
            ".ex", ".exs", ".erl", ".hrl"
    );

    private static final Set<String> WEB_TECHNOLOGIES = createSet(
            ".html", ".css", ".scss", ".sass", ".less", ".styl",
            ".vue", ".svelte", ".jsx", ".tsx", ".astro",
            ".graphql", ".gql"
    );

    private static final Set<String> DATA_AND_CONFIG = createSet(
            ".xml", ".json", ".yml", ".yaml", ".properties",
            ".toml", ".ini", ".cfg", ".env", ".proto",
            ".avsc", ".xsd", ".plist"
    );

    private static final Set<String> OTHER_EXTENSIONS = createSet(
            ".gitignore", ".gitattributes", ".txt", ".md", ".rst",
            ".adoc", ".asciidoc", ".tex", ".org", ".sh", ".bat",
            ".ps1", ".zsh", ".fish", ".csh", ".awk", ".sed",
            ".gradle", ".sql", ".maven", ".pom", ".cmake",
            ".sbt", ".bazel", ".bzl", ".nix", ".mk", ".dockerfile",
            ".editorconfig", ".prettierrc", ".eslintrc"
    );

    private static final Set<String> ALL_EXTENSIONS;

    static {
        Set<String> combined = new HashSet<>();
        combined.addAll(PROGRAMMING_LANGUAGES);
        combined.addAll(WEB_TECHNOLOGIES);
        combined.addAll(DATA_AND_CONFIG);
        combined.addAll(OTHER_EXTENSIONS);
        ALL_EXTENSIONS = Collections.unmodifiableSet(combined);
    }

    private FileExtensionConfig() {
    }

    public static Set<String> getAllSupportedExtensions() {
        return ALL_EXTENSIONS;
    }

    public static boolean isSupported(String extension) {
        return ALL_EXTENSIONS.contains(extension.toLowerCase());
    }

    private static Set<String> createSet(String... items) {
        return Collections.unmodifiableSet(new HashSet<>(Arrays.asList(items)));
    }
}
