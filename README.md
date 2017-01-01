# maven-enforcer-property-usage

Ensure that all the Java properties defined in resources .properties files
are also used in code, all properties used in code are also defined and
no property is defined more than once.

## Maven Enforcer

* [Apache's Maven Enforcer Plugin](http://maven.apache.org/plugins/maven-enforcer-plugin/) is used to apply and enforce rules on your Maven projects.
* The Enforcer plugin ships with a set of [standard rules](http://maven.apache.org/enforcer/enforcer-rules/index.html).
* This project provides an extra rule (enforceProperties) which is not part of the standard rule set.
* More extra rules can be found [MojoHaus Project extra rules](http://www.mojohaus.org/extra-enforcer-rules/index.html) which are not part of the standard rule set.

## Links

* [![Apache License, Version 2.0, January 2004](https://img.shields.io/github/license/mojohaus/extra-enforcer-rules.svg?label=License)](http://www.apache.org/licenses/)
* [![Maven Central](https://img.shields.io/maven-central/v/com.github.mikkoi.maven.enforcer.rule/property-usage-rule.svg?label=Maven%20Central)](http://search.maven.org/#search%7Cga%7C1%7Cg%3A%22com.github.mikkoi.maven.enforcer.rule%22%20a%3A%22property-usage-rule%22)
* [Project Website](http://mikkoi.github.io/maven-enforcer-property-usage/)
* [SonarQube Reports](https://sonarqube.com/dashboard/index/com.github.mikkoi.maven.enforcer.rule:property-usage-rule)
* [![Build Status](https://travis-ci.org/mikkoi/maven-enforcer-property-usage.svg?branch=master)](https://travis-ci.org/mikkoi/maven-enforcer-property-usage)

## Quickstart

Add the following to your project's `pom.xml` file:

    <plugin>
        <groupId>org.apache.maven.plugins</groupId>
        <artifactId>maven-enforcer-plugin</artifactId>
        <dependencies>
            <dependency>
                <groupId>com.github.mikkoi.maven.enforcer.rule</groupId>
                <artifactId>property-usage-rule</artifactId>
            </dependency>
        </dependencies>
        <executions>
            <execution>
                <id>enforce-property-usage</id>
                <goals>
                    <goal>enforce</goal>
                </goals>
                <configuration>
                    <rules>
                        <enforceProperties implementation="com.github.mikkoi.maven.plugins.enforcer.rule.propertyusage.PropertyUsageRule">
                            <!-- default: "${project.build.sourceEncoding}" -->
                            <sourceEncoding>UTF-8</sourceEncoding>
                            <!-- default: "${project.build.sourceEncoding}" -->
                            <propertiesEncoding>UTF-8</propertiesEncoding>
                            <definitionsOnlyOnce>true</definitionsOnlyOnce>
                            <definedPropertiesAreUsed>true</definedPropertiesAreUsed>
                            <usedPropertiesAreDefined>true</usedPropertiesAreDefined>
                            <replaceInTemplateWithPropertyName>REPLACE_THIS<replaceInTemplateWithPropertyName>
                            <definitions>
                                <!-- The files from which to read the used properties. -->
                                <!-- default: src/main/resources/**/*.properties -->
                                <!-- file is either a path to a file or dir. -->
                                <!-- Patterns (wildcards) not supported at the moment. -->
                                <!-- When directory, all files in it are checked recursively. -->
                                <file></file>
                            </definitions>
                            <templates>
                                <!-- The usage templates. -->
                                <!-- default: property name (as it is in *.properties files) -->
                                <!-- in quotation marks -->
                                <template></template>
                            </templates>
                            <usages>
                                <!-- The files to check for the used properties. -->
                                <!-- default: src/main/java/**/*.java -->
                                <!-- file is either a path to a file or dir. -->
                                <!-- Patterns (wildcards) not supported at the moment. -->
                                <!-- When directory, all files in it are checked recursively. -->
                                <file></file>
                            </usages>
                        </enforceProperties>
                    </rules>
                    <!-- Fail build if rule broken. -->
                    <fail>true</fail>
                </configuration>
            </execution>
        </executions>
    </plugin>

