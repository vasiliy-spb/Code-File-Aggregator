package dev.cheercode;

public class FileAggregatorApp {
    public static void main(String[] args) {
        // Dependency Injection - создаем все зависимости
        FileFilter fileFilter = new CodeFileFilter();
        OutputFormatter formatter = new TxtOutputFormatter();
        ProcessorFactory processorFactory = new ProcessorFactory(fileFilter, formatter);
        ConsoleUserInterface userInterface = new ConsoleUserInterface();

        // Создаем главный объект приложения
        FileAggregator aggregator = new FileAggregator(userInterface, processorFactory, formatter);

        try {
            // Запускаем приложение
            aggregator.run();
        } finally {
            // Освобождаем ресурсы
            userInterface.close();
        }
    }
}
