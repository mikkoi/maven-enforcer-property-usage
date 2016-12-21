package com.github.mikkoi.maven.plugins.enforcer.rule.propertyusage.configuration;

import java.util.Collection;
import java.util.Collections;

public class Templates {

    public static final Collection<Template> DEFAULT = Collections.singletonList(
            new Template("([[:print:]]+)")
    );

    private Templates() {}
}

