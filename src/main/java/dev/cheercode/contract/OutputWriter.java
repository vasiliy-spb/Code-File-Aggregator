package dev.cheercode.contract;

import java.io.IOException;

public interface OutputWriter extends AutoCloseable {
    void println(String line);
    void println();

    void printf(String format, Object... args);

    @Override
    void close() throws IOException;
}