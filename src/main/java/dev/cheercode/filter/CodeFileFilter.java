package dev.cheercode.filter;

import dev.cheercode.contract.FileFilter;
import dev.cheercode.filter.rules.FileInclusionRules;

public final class CodeFileFilter implements FileFilter {

    @Override
    public boolean shouldInclude(String filePath) {
        return FileInclusionRules.shouldInclude(filePath);
    }
}
