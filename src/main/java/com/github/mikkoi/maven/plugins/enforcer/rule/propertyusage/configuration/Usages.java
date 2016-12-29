package com.github.mikkoi.maven.plugins.enforcer.rule.propertyusage.configuration;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.Collections;

import static com.github.mikkoi.maven.plugins.enforcer.rule.propertyusage.configuration.Files.absoluteCwdAndFile;

public class Usages {

    private Usages() {
    }

    /**
     * Get Default value
     *
     * @return collection of strings
     */
    @Nonnull
    public static Collection<String> getDefault() {
        // Future solution: absoluteCwdAndFile("src/main/java/[[:print:]]+\\.java")
        return Collections.singleton(absoluteCwdAndFile("src/main/java"));
    }
}
