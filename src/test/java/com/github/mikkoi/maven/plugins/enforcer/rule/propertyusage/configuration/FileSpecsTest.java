package com.github.mikkoi.maven.plugins.enforcer.rule.propertyusage.configuration;

import com.github.mikkoi.maven.plugins.enforcer.rule.propertyusage.TestEnforcerLoggerFactory;
import org.apache.maven.enforcer.rule.api.EnforcerLogger;
import org.apache.maven.plugin.logging.SystemStreamLog;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

import static com.github.mikkoi.maven.plugins.enforcer.rule.propertyusage.configuration.FileSpecs.absoluteCwdAndFile;
import static org.junit.Assert.assertEquals;

public class FileSpecsTest {

    @Rule
    public TemporaryFolder testDir = new TemporaryFolder();

    public EnforcerLogger enforcerLogger = TestEnforcerLoggerFactory.createTestEnforcerLogger();

    @Test
    public void getFilenamesFromFileSpecs() throws Exception {
        SystemStreamLog slog = new SystemStreamLog();

        File tempFile = testDir.newFile();
        Collection<String> files = Arrays.asList(
                "src/test/resources/properties-dir/**/*.properties",
                "src/test/resources/app1.properties",
                tempFile.getAbsolutePath()
        );
        Collection<String> expected = Arrays.asList(
                absoluteCwdAndFile("src/test/resources/properties-dir/sub-app-props1.properties"),
                absoluteCwdAndFile("src/test/resources/properties-dir/sub-app-props2.properties"),
                absoluteCwdAndFile("src/test/resources/app1.properties"),
                tempFile.getAbsolutePath()
        );
        expected = expected.stream().sorted().collect(Collectors.toSet());
        slog.debug("current dir:"+ Paths.get("").toAbsolutePath().toString());
        Collection<String> filenames = FileSpecs
                .getAbsoluteFilenames(files, Paths.get("").toAbsolutePath(), enforcerLogger);
        filenames = filenames.stream().sorted().collect(Collectors.toSet());
        assertEquals("Filenames are as expected.", expected, filenames);
    }

    @Test
    public void getFilenamesFromFileSpecs_noFiles() throws Exception {
        Collection<String> files = Arrays.asList(
                absoluteCwdAndFile("src/test/resources/no-exist-dir/*.properties"),
                absoluteCwdAndFile("src/test/resources/no-way.properties")
        );
        Collection<String> expected = Collections.emptySet();
        Collection<String> filenames = new HashSet<>(FileSpecs
                .getAbsoluteFilenames(files, Paths.get("").toAbsolutePath(), enforcerLogger));
        assertEquals("Filenames are as expected (none).", expected, filenames);
    }

    @Test
    public void getFilenamesFromFileSpecsWithFilesEnteredMultipleTimes() throws Exception {
        SystemStreamLog slog = new SystemStreamLog();

        Collection<String> files = Arrays.asList(
                "src/test/resources/properties-dir/**/*.properties",
                "src/test/resources/app1.properties",
                "src/test/resources/properties-dir/sub-app-props1.properties",
                "src/test/resources/*/*.properties"
        );
        Collection<String> expected = Arrays.asList(
                absoluteCwdAndFile("src/test/resources/properties-dir/sub-app-props1.properties"),
                absoluteCwdAndFile("src/test/resources/properties-dir/sub-app-props2.properties"),
                absoluteCwdAndFile("src/test/resources/app1.properties")
        );
        expected = expected.stream().sorted().collect(Collectors.toSet());
        slog.debug("current dir:"+ Paths.get("").toAbsolutePath().toString());
        Collection<String> filenames = FileSpecs
                .getAbsoluteFilenames(files, Paths.get("").toAbsolutePath(), enforcerLogger);
        filenames = filenames.stream().sorted().collect(Collectors.toSet());
        assertEquals("Filenames are as expected.", expected, filenames);
    }

    // TODO Collect the log, verify log entry:
    // [error] File spec '' is blank. Error in configuration!
    @Test
    public void getErrorWhenAnEmptyStringIsGiven() throws Exception {
        SystemStreamLog slog = new SystemStreamLog();

        Collection<String> files = Arrays.asList(
                "src/test/resources/app1.properties",
                "",
                "src/test/resources/properties-dir/sub-app-props1.properties"
                );
        Collection<String> expected = new HashSet<>(Arrays.asList(
                absoluteCwdAndFile("src/test/resources/properties-dir/sub-app-props1.properties"),
                absoluteCwdAndFile("src/test/resources/app1.properties")
        ));
        slog.debug("current dir:"+ Paths.get("").toAbsolutePath().toString());
        Collection<String> filenames = new HashSet<>(FileSpecs
                .getAbsoluteFilenames(files, Paths.get("").toAbsolutePath(), enforcerLogger));
        assertEquals("Filenames are as expected.", expected, filenames);
    }

}
