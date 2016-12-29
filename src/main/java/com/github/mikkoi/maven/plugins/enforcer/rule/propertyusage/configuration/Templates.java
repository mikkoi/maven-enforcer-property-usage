package com.github.mikkoi.maven.plugins.enforcer.rule.propertyusage.configuration;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.Collections;

public class Templates {

    private Templates() {
    }

    /**
     * Get Default value
     * @return collection of strings
     */
    @Nonnull
    public static Collection<String> getDefault() {
        return Collections.singleton(
                "\"(REPLACE_THIS)\""
//                "\"(\\S+)\""
//                "\"my.property.value\""
        );
    }

    /**
     * Get Default value
     * @return default string to replace in template
     */
    @Nonnull
    public static String getDefaultReplaceInTemplateWithPropertyName() {
        return "REPLACE_THIS";
    }
}

