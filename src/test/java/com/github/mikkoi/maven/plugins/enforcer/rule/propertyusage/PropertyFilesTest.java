package com.github.mikkoi.maven.plugins.enforcer.rule.propertyusage;

import com.google.common.collect.Maps;
import org.junit.Test;

import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class PropertyFilesTest {

    @Test
    public void readPropertiesFromFiles_Normal() throws Exception {
        Map<String, Integer> expected = Maps.newHashMap();
        expected.put("my.property.value", 1);
        expected.put("other.prop.val", 1);
        expected.put("my-too.property.value", 2);
        expected.put("other-too.prop.val", 1);
        expected.put("other-three.prop.val", 1);
        Collection<String> filenames = Arrays.asList(
                "src/test/resources/app1.properties",
                "src/test/resources/app2.properties",
                "src/test/resources/app3.properties"
        );
        Map<String, Integer> properties = PropertyFiles.readPropertiesFromFiles(filenames);
        assertEquals("Read properties are as expected.", expected, properties);
    }

    @Test(expected = FileNotFoundException.class)
    public void readPropertiesFromFiles_ExceptionBecauseFileNotExists() throws Exception {
        Collection<String> filenames = Collections.singletonList(
                "src/test/resources/not-exists.properties"
        );
        Map<String, Integer> properties = PropertyFiles.readPropertiesFromFiles(filenames);
        assertEquals(null, properties);
    }

    @Test
    public void readPropertiesFromFiles_EmptyFilesAreOK() throws Exception {
        Map<String, Integer> expected = Maps.newHashMap();
        Collection<String> filenames = Collections.singletonList(
                "src/test/resources/empty.properties"
        );
        Map<String, Integer> properties = PropertyFiles.readPropertiesFromFiles(filenames);
        assertEquals("Read properties are as expected (empty)", expected, properties);
    }

}