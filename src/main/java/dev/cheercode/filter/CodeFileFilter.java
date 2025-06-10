package dev.cheercode.filter;

import dev.cheercode.contract.FileFilter;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class CodeFileFilter implements FileFilter {
    private static final Set<String> ALLOWED_EXTENSIONS = new HashSet<>(Arrays.asList(
            // Языки программирования
            ".java",         // Java
            ".js",           // JavaScript
            ".ts",           // TypeScript
            ".py",           // Python
            ".cpp",          // C++
            ".c",            // C
            ".h",            // C/C++ заголовочные файлы
            ".hpp",          // C++ заголовочные файлы
            ".cs",           // C#
            ".php",          // PHP
            ".go",           // Go
            ".rs",           // Rust
            ".kt", ".kts",   // Kotlin
            ".swift",        // Swift
            ".rb",           // Ruby
            ".pl", ".pm",    // Perl
            ".r",            // R
            ".scala",        // Scala
            ".clj", ".cljs", // Clojure
            ".dart",         // Dart
            ".lua",          // Lua
            ".vb",           // Visual Basic
            ".fs", ".fsx",   // F#
            ".elm",          // Elm
            ".ex", ".exs",   // Elixir
            ".erl", ".hrl",  // Erlang
            ".hs",           // Haskell
            ".ml", ".mli",   // OCaml
            ".jl",           // Julia
            ".nim",          // Nim
            ".zig",          // Zig
            ".v",            // V
            ".d",            // D
            ".m", ".mm",     // Objective-C

            // Веб-разработка
            ".html", ".css",  // Основные веб-технологии
            ".scss", ".sass", ".less", ".styl", // CSS препроцессоры
            ".vue",          // Vue.js
            ".svelte",       // Svelte
            ".jsx", ".tsx",  // React
            ".astro",        // Astro
            ".graphql", ".gql", // GraphQL

            // Данные и конфигурация
            ".xml", ".json", // Структурированные данные
            ".yml", ".yaml", // YAML конфиги
            ".properties",  // Properties файлы
            ".toml",         // TOML конфигурация
            ".ini", ".cfg",  // INI конфигурация
            ".env",          // Environment файлы
            ".proto",        // Protocol Buffers
            ".avsc",         // Avro Schema
            ".xsd",          // XML Schema
            ".plist",        // Property List (iOS/macOS)

            // Системы контроля версий
            ".gitignore", ".gitattributes", // Git файлы

            // Документация и текстовые файлы
            ".txt",         // Простой текст
            ".md",          // Markdown
            ".rst",          // reStructuredText
            ".adoc", ".asciidoc", // AsciiDoc
            ".tex",          // LaTeX
            ".org",          // Org-mode

            // Скрипты и автоматизация
            ".sh", ".bat", ".ps1", // Основные скрипты
            ".zsh", ".fish", ".csh", // Различные shell'ы
            ".awk",          // AWK
            ".sed",          // SED скрипты
            ".gradle",       // Gradle скрипты
            ".sql",          // SQL скрипты

            // Системы сборки
            ".maven", ".pom", // Maven
            ".cmake",        // CMake
            ".sbt",          // Scala Build Tool
            ".bazel", ".bzl", // Bazel
            ".nix",          // Nix
            ".mk",           // Makefile альтернативное расширение
            ".dockerfile",   // Dockerfile альтернативное расширение

            // Инструменты разработки
            ".editorconfig", // EditorConfig
            ".prettierrc",   // Prettier
            ".eslintrc"      // ESLint
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
                fileName.equals("go.mod") ||
                fileName.equals("go.sum") ||           // Go dependencies
                fileName.equals("Pipfile") ||          // Python Pipenv
                fileName.equals("poetry.lock") ||      // Python Poetry
                fileName.equals("yarn.lock") ||        // Yarn lock file
                fileName.equals("package-lock.json") || // NPM lock file
                fileName.equals("composer.json") ||    // PHP Composer
                fileName.equals("composer.lock") ||    // PHP Composer lock
                fileName.equals("Gemfile") ||          // Ruby Bundler
                fileName.equals("Gemfile.lock") ||     // Ruby Bundler lock
                fileName.equals("mix.exs") ||          // Elixir Mix
                fileName.equals("Project.toml") ||     // Julia
                fileName.equals("pyproject.toml") ||   // Python modern config
                fileName.equals("deno.json") ||        // Deno config
                fileName.equals("bun.lockb") ||        // Bun lock file
                fileName.equals("pubspec.yaml") ||     // Dart/Flutter
                fileName.equals("build.sbt") ||        // Scala SBT
                fileName.equals("CMakeLists.txt") ||   // CMake
                fileName.equals("meson.build") ||      // Meson build
                fileName.equals("BUILD") ||            // Bazel BUILD file
                fileName.equals("WORKSPACE") ||        // Bazel WORKSPACE
                fileName.equals("flake.nix") ||        // Nix flakes
                fileName.equals("shell.nix") ||        // Nix shell
                fileName.equals("default.nix");        // Nix default
    }

    private boolean isDocumentationFile(String fileName) {
        return fileName.toLowerCase().startsWith("readme") ||
                fileName.toLowerCase().startsWith("changelog") ||
                fileName.toLowerCase().startsWith("license");
    }
}