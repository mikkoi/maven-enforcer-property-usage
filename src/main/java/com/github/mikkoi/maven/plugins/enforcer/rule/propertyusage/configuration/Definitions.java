package com.github.mikkoi.maven.plugins.enforcer.rule.propertyusage.configuration;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.dataflow.qual.Pure;

import java.util.Collection;
import java.util.Collections;

public class Definitions {

    private Definitions() {
		// This class cannot be instantiated.
		throw new AssertionError();
    }

    /**
     * Get Default value
     *
     * @return collection of strings
     */
    @Pure
    public static @NonNull Collection<String> getDefault() {
        return Collections.singleton("src/main/resources/**/*.properties");
    }
}
