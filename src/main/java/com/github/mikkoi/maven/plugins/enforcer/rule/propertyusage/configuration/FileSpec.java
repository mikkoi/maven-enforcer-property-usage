package com.github.mikkoi.maven.plugins.enforcer.rule.propertyusage.configuration;

import javax.annotation.Nonnull;

public class FileSpec {
    @Nonnull
    private final String value;

    public FileSpec(@Nonnull final String file) {
        value = file;
    }

    @Nonnull
    public String getFile() {
        return value;
    }
}
