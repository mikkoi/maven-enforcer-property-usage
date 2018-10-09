package com.github.mikkoi.maven.plugins.enforcer.rule.propertyusage;

import org.apache.maven.plugin.logging.SystemStreamLog;
import org.junit.Test;

import java.io.FileNotFoundException;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class PropertyFilesTest {

    private SystemStreamLog slog = new SystemStreamLog();

    @Test
    public void readPropertiesFromFiles_Normal() throws Exception {
        Map<String, Integer> expected = new HashMap<>();
        expected.put("my.property.value", 1);
        expected.put("other.prop.val", 1);
        expected.put("my-too.property.value", 1);
        expected.put("other-too.prop.val", 1);
        expected.put("also-prop.val", 1);
        expected.put("my-first.property.value", 1);
        expected.put("my-second.prop.val", 1);
        expected.put("my-third-val", 1);
        Collection<String> filenames = Arrays.asList(
                "src/test/resources/app1.properties",
                "src/test/resources/app2.properties",
                "src/test/resources/app3.properties"
        );
        PropertyFiles propertyFiles = new PropertyFiles(slog, Charset.forName("UTF-8"));
        Map<String, Integer> properties = propertyFiles.readPropertiesFromFilesWithoutCount(filenames);
        assertEquals("Read properties are as expected.", expected, properties);
    }

    @Test(expected = FileNotFoundException.class)
    public void readPropertiesFromFiles_ExceptionBecauseFileNotExists() throws Exception {
        Collection<String> filenames = Collections.singletonList(
                "src/test/resources/not-exists.properties"
        );
        PropertyFiles propertyFiles = new PropertyFiles(slog, Charset.forName("UTF-8"));
        propertyFiles.readPropertiesFromFilesWithoutCount(filenames);
    }

    @Test
    public void readPropertiesFromFiles_EmptyFilesAreOK() throws Exception {
        Map<String, Integer> expected = new HashMap<>();
        Collection<String> filenames = Collections.singletonList(
                "src/test/resources/empty.properties"
        );
        PropertyFiles propertyFiles = new PropertyFiles(slog, Charset.forName("UTF-8"));
        Map<String, Integer> properties = propertyFiles.readPropertiesFromFilesWithoutCount(filenames);
        assertEquals("Read properties are as expected (empty)", expected, properties);
    }

    @Test
    public void readPropertiesFromTestPropertyFile() throws Exception {
        Map<String, Integer> expected = new HashMap<>();
        expected.put("first.property.value", 2);
        expected.put("second.property.value", 1);
        expected.put("third.property.value", 4);
        expected.put("fourth.property.value", 1);
        Collection<String> filenames = Collections.singleton(
                "src/test/resources/app1-double-def.properties"
        );
        PropertyFiles propertyFiles = new PropertyFiles(slog, Charset.forName("UTF-8"));
        Map<String, Integer> properties = propertyFiles.readPropertiesFromFilesWithCount(filenames);
        assertEquals("Read properties are as expected.", expected, properties);
    }

}
