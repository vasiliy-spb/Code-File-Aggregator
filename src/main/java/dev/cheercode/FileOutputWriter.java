package dev.cheercode;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;

public class FileOutputWriter implements OutputWriter {
    private final PrintWriter writer;

    public FileOutputWriter(String outputPath) throws IOException {
        this.writer = new PrintWriter(
                new OutputStreamWriter(
                        new FileOutputStream(outputPath),
                        StandardCharsets.UTF_8
                )
        );
    }

    @Override
    public void println(String line) {
        writer.println(line);
    }

    @Override
    public void println() {
        writer.println();
    }

    @Override
    public void printf(String format, Object... args) {
        writer.printf(format, args);
    }

    @Override
    public void close() throws IOException {
        writer.close();
    }
}