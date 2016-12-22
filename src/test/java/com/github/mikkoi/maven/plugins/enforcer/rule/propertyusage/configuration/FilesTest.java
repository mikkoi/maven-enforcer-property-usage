package com.github.mikkoi.maven.plugins.enforcer.rule.propertyusage.configuration;

import org.apache.maven.plugin.logging.SystemStreamLog;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.stream.Collectors;

import static com.github.mikkoi.maven.plugins.enforcer.rule.propertyusage.configuration.Files.absoluteCwdAndFile;
import static org.junit.Assert.assertEquals;

public class FilesTest {

    @Test
    public void getFilenamesFromFileSpecs() throws Exception {
        SystemStreamLog slog = new SystemStreamLog();
        Collection<String> files = Arrays.asList(
                absoluteCwdAndFile("src/test/resources/properties-dir"),
                absoluteCwdAndFile("src/test/resources/app1.properties")
        );
        Collection<String> expected = Arrays.asList(
                absoluteCwdAndFile("src/test/resources/properties-dir/sub-app-props1.properties"),
                absoluteCwdAndFile("src/test/resources/properties-dir/sub-app-props2.properties"),
                absoluteCwdAndFile("src/test/resources/app1.properties")
        );
        expected = expected.stream().sorted().collect(Collectors.toSet());
        Files fileSpecs = new Files(slog);
        Collection<String> filenames = fileSpecs.getAbsoluteFilenames(files);
        filenames = filenames.stream().sorted().collect(Collectors.toSet());
        assertEquals("Filenames are as expected.", expected, filenames);
    }

    @Test
    public void getFilenamesFromFileSpecs_noFiles() throws Exception {
        SystemStreamLog slog = new SystemStreamLog();
        Collection<String> files = Arrays.asList(
                absoluteCwdAndFile("src/test/resources/no-exist-dir"),
                absoluteCwdAndFile("src/test/resources/no-way.properties")
        );
        Collection<String> expected = Collections.emptySet();
        expected = expected.stream().sorted().collect(Collectors.toSet());
        Files fileSpecs = new Files(slog);
        Collection<String> filenames = fileSpecs.getAbsoluteFilenames(files);
        filenames = filenames.stream().sorted().collect(Collectors.toSet());
        assertEquals("Filenames are as expected (none).", expected, filenames);
    }
}