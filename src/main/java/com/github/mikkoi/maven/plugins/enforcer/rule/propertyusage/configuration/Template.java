package com.github.mikkoi.maven.plugins.enforcer.rule.propertyusage.configuration;

import javax.annotation.Nonnull;

public class Template {
    @Nonnull
    private final String value;

    public Template(@Nonnull final String tpl) {
        value = tpl;
    }

    @Nonnull
    public String getTemplate() {
        return value;
    }
}
