package com.github.mikkoi.maven.plugins.enforcer.rule.propertyusage.configuration;

import java.util.Collection;
import java.util.Collections;

public class Definitions {

    public static final Collection<Collection<FileSpec>> DEFAULT = Collections.singletonList(
            Collections.singletonList(new FileSpec("src/main/java/[[:print:]]+\\.java"))
    );

    private Definitions() {}
}
