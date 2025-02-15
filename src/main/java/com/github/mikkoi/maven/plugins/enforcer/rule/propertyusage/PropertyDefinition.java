package com.github.mikkoi.maven.plugins.enforcer.rule.propertyusage;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.Objects;

public class PropertyDefinition {


    private final @NonNull String key;
    private final @NonNull String value;
    private final @NonNull String filename;
    private final int linenumber;

    public PropertyDefinition(final @NonNull String key, final @NonNull String value, final @NonNull String filename,
                              final int linenumber) {
        Objects.requireNonNull(key);
        Objects.requireNonNull(value);
        Objects.requireNonNull(filename);
        this.key = key;
        this.value = value;
        this.filename = filename;
        this.linenumber = linenumber;
    }

    public @NonNull String getKey() {
        return key;
    }

    public @NonNull String getValue() {
        return value;
    }

    public @NonNull String getFilename() {
        return filename;
    }

    public int getLinenumber() {
        return linenumber;
    }

}
