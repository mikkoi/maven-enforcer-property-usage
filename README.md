# maven-enforcer-property-usage

Maven Enforcer Plugin (org.apache.maven.plugins:maven-enforcer-plugin) rule:
Ensure that all the Java properties defined in resources .properties files
are also used in code.

## Links

* [Project Website](http://mikkoi.github.io/maven-enforcer-property-usage/)
* [SonarQube Reports](https://sonarqube.com/dashboard/index/com.github.mikkoi.maven.enforcer.rule:property-usage-rule)

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
                            <definitionsOnlyOnce>true</definitionsOnlyOnce>
                            <definedPropertiesAreUsed>true</definedPropertiesAreUsed>
                            <usedPropertiesAreDefined>true</usedPropertiesAreDefined>
                            <replaceInTemplateWithPropertyName>REPLACE_THIS<replaceInTemplateWithPropertyName>
                            <definitions>
                                <!-- The files from which to read the used properties. -->
                                <!-- default: src/main/resources/*.properties -->
                                <!-- file is either a path to a file or dir. -->
                                <!-- Patterns (wildcards) not supported at the moment. -->
                                <!-- When directory, all files in it are checked recursively. -->
                                <file></file>
                            </definitions>
                            <templates>
                                <!-- The usage templates. -->
                                <!-- default: property name as it is in *.properties files, -->
                                <!-- in quotation marks -->
                                <template></template>
                            </templates>
                            <usages>
                                <!-- The files to check for the used properties. -->
                                <!-- default: src/main/java -->
                                <!-- file is either a path to a file or dir. -->
                                <!-- Patterns (wildcards) not supported at the moment. -->
                                <!-- When directory, all files in it are checked recursively. -->
                                <files>
                                    <file></file>
                                </files>
                            </usages>
                        </enforceProperties>
                    </rules>
                    <!-- Fail build if wrong encoding encountered. -->
                    <fail>true</fail>
                </configuration>
            </execution>
        </executions>
    </plugin>

