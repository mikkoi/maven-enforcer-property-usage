package com.github.mikkoi.maven.plugins.enforcer.rule.propertyusage.configuration;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.Collections;

public class Templates {

    /**
     * Replace template property name placeholder with this when searching for properties.
     */
    public static final String PROPERTY_NAME_REGEXP = "([a-z0-9\\-\\.]{1,}?)";

    /**
     * Default property name placeholder string to replace in template.
     */
    public static final String DEFAULT_REPLACE_IN_TEMPLATE_WITH_PROPERTY_NAME = "REPLACE_THIS";

    private Templates() {
    }

    /**
     * Get Default value
     *
     * @return collection of strings
     */
    @Nonnull
    public static Collection<String> getDefault() {
        return Collections.singleton("\"" + DEFAULT_REPLACE_IN_TEMPLATE_WITH_PROPERTY_NAME + "\"");
    }

}

