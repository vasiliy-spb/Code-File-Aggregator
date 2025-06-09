package dev.cheercode;

import dev.cheercode.core.FileAggregator;
import dev.cheercode.core.ProcessorFactory;
import dev.cheercode.filter.CodeFileFilter;
import dev.cheercode.formatter.TxtOutputFormatter;
import dev.cheercode.contract.FileFilter;
import dev.cheercode.contract.OutputFormatter;
import dev.cheercode.ui.ConsoleUserInterface;

public class FileAggregatorApp {
    public static void main(String[] args) {
        // Dependency Injection - создаем все зависимости
        FileFilter fileFilter = new CodeFileFilter();
        OutputFormatter formatter = new TxtOutputFormatter();
        ProcessorFactory processorFactory = new ProcessorFactory(fileFilter, formatter);
        ConsoleUserInterface userInterface = new ConsoleUserInterface();

        // Создаем главный объект приложения
        FileAggregator aggregator = new FileAggregator(userInterface, processorFactory, formatter, fileFilter);

        try {
            // Запускаем приложение
            aggregator.run();
        } finally {
            // Освобождаем ресурсы
            userInterface.close();
        }
    }
}
