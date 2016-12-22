package com.github.mikkoi.maven.plugins.enforcer.rule.propertyusage.configuration;

import java.util.Collection;
import java.util.Collections;

import static com.github.mikkoi.maven.plugins.enforcer.rule.propertyusage.configuration.Files.absoluteCwdAndFile;

public class Usages {

    public static final Collection<Collection<String>> DEFAULT = Collections.singletonList(
            // Future solution: absoluteCwdAndFile("src/main/java/[[:print:]]+\\.java")
            Collections.singleton(absoluteCwdAndFile("src/main/java"))
    );

    private Usages() {}

}
