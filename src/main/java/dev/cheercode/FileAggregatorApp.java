package dev.cheercode;

import dev.cheercode.core.FileAggregator;
import dev.cheercode.filter.CodeFileFilter;
import dev.cheercode.formatter.TxtOutputFormatter;
import dev.cheercode.contract.FileFilter;
import dev.cheercode.contract.OutputFormatter;
import dev.cheercode.ui.ConsoleUserInterface;

public class FileAggregatorApp {
    public static void main(String[] args) {
        FileFilter fileFilter = new CodeFileFilter();
        OutputFormatter formatter = new TxtOutputFormatter();
        ConsoleUserInterface userInterface = new ConsoleUserInterface();

        FileAggregator aggregator = new FileAggregator(userInterface, formatter, fileFilter);

        try {
            aggregator.run();
        } finally {
            userInterface.close();
        }
    }
}
