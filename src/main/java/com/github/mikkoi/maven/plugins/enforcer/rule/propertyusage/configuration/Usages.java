package com.github.mikkoi.maven.plugins.enforcer.rule.propertyusage.configuration;

import java.util.Collection;
import java.util.Collections;

public class Usages {

    public static final Collection<Collection<FileSpec>> DEFAULT = Collections.singletonList(
            Collections.singleton(new FileSpec("src/main/java/[[:print:]]+\\.java"))
    );

    private Usages() {}

}
