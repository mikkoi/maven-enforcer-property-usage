# maven-enforcer-property-usage

Maven Enforcer Plugin (org.apache.maven.plugins:maven-enforcer-plugin) rule:
Ensure that all the Java properties defined in resources .properties files
are also used in code.

---

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
                        <useAllProperties implementation="com.github.mikkoi.maven.plugins.enforcer.rule.propertyusage.PropertyUsageRule">
                            <properties>
                                <!-- The files from which to read the used properties. -->
                                <!-- default: src/main/resources/*.properties -->
                                <!-- File is always used as a regular expression. -->
                                <!-- When directory, all files are checked recursively. -->
                                <files>
                                    <file></file>
                                </file>
                            </properties>
                            <templates>
                                <!-- The usage templates. -->
                                <!-- default: property name as it is in *.properties files -->
                                <template></template>
                            </templates>
                            <source>
                                <!-- The files to check for the used properties. -->
                                <!-- default: src/main/java -->
                                <!-- File is always used as a regular expression. -->
                                <!-- When directory, all files are checked recursively. -->
                                <file></file>
                            </source>
                        </useAllProperties>
                    </rules>
                    <!-- Fail build if wrong encoding encountered. -->
                    <fail>true</fail>
                </configuration>
            </execution>
        </executions>
    </plugin>

