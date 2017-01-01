package com.github.mikkoi.maven.plugins.enforcer.rule.propertyusage.configuration;

import org.apache.maven.plugin.logging.SystemStreamLog;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.stream.Collectors;

import static com.github.mikkoi.maven.plugins.enforcer.rule.propertyusage.configuration.FileSpecs.absoluteCwdAndFile;
import static org.junit.Assert.assertEquals;

public class FileSpecsTest {

    @Rule
    public TemporaryFolder testDir = new TemporaryFolder();

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
        Collection<String> filenames = FileSpecs.getAbsoluteFilenames(files, Paths.get(""), slog);
        filenames = filenames.stream().sorted().collect(Collectors.toSet());
        assertEquals("Filenames are as expected.", expected, filenames);
    }

    @Test
    public void getFilenamesFromFileSpecs_noFiles() throws Exception {
        SystemStreamLog slog = new SystemStreamLog();
        Collection<String> files = Arrays.asList(
                absoluteCwdAndFile("src/test/resources/no-exist-dir/*.properties"),
                absoluteCwdAndFile("src/test/resources/no-way.properties")
        );
        Collection<String> expected = Collections.emptySet();
        expected = expected.stream().sorted().collect(Collectors.toSet());
        Collection<String> filenames = FileSpecs.getAbsoluteFilenames(files, Paths.get(""), slog);
        filenames = filenames.stream().sorted().collect(Collectors.toSet());
        assertEquals("Filenames are as expected (none).", expected, filenames);
    }
}
