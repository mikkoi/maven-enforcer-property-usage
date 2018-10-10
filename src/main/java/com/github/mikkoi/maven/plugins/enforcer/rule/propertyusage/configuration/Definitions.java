package com.github.mikkoi.maven.plugins.enforcer.rule.propertyusage.configuration;

import javax.annotation.Nonnull;
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
    @Nonnull
    public static Collection<String> getDefault() {
        return Collections.singleton("src/main/resources/**/*.properties");
    }
}
