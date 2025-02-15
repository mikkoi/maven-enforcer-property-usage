package com.github.mikkoi.maven.plugins.enforcer.rule.propertyusage;

import org.apache.maven.enforcer.rule.api.EnforcerLogger;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.plugin.logging.SystemStreamLog;
import org.junit.Test;

import java.nio.charset.StandardCharsets;
import java.nio.file.NoSuchFileException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import static org.junit.Assert.assertEquals;

public class PropertyFilesTest {

//    private SystemStreamLog enforcerLogger = new SystemStreamLog();

    public EnforcerLogger enforcerLogger = new EnforcerLogger() {
        final Log log = new SystemStreamLog();

        @Override
        public void warnOrError(CharSequence message) {

        }

        @Override
        public void warnOrError(Supplier<CharSequence> messageSupplier) {

        }

        @Override
        public boolean isDebugEnabled() {
//            return log.isDebugEnabled();
            return false;
        }

        @Override
        public void debug(CharSequence message) {
//            log.debug(message);
        }

        @Override
        public void debug(Supplier<CharSequence> messageSupplier) {
//            if (log.isDebugEnabled()) {
//                log.debug(messageSupplier.get());
//            }
        }

        @Override
        public boolean isInfoEnabled() {
//            return log.isInfoEnabled();
            return false;
        }

        @Override
        public void info(CharSequence message) {
            log.info(message);
        }

        @Override
        public void info(Supplier<CharSequence> messageSupplier) {
            if (log.isInfoEnabled()) {
                log.info(messageSupplier.get());
            }
        }

        @Override
        public boolean isWarnEnabled() {
            return log.isWarnEnabled();
        }

        @Override
        public void warn(CharSequence message) {
            log.warn(message);
        }

        @Override
        public void warn(Supplier<CharSequence> messageSupplier) {
            if (log.isWarnEnabled()) {
                log.warn(messageSupplier.get());
            }
        }

        @Override
        public boolean isErrorEnabled() {
            return log.isErrorEnabled();
        }

        @Override
        public void error(CharSequence message) {
            log.error(message);
        }

        @Override
        public void error(Supplier<CharSequence> messageSupplier) {
            if (log.isErrorEnabled()) {
                log.error(messageSupplier.get());
            }
        }
    };


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
        PropertyFiles propertyFiles = new PropertyFiles(enforcerLogger, StandardCharsets.UTF_8);
        Map<String, Integer> properties = propertyFiles.readPropertiesFromFilesWithoutCount(filenames);
        assertEquals("Read properties are as expected.", expected, properties);
    }

    @Test(expected = NoSuchFileException.class)
    public void readPropertiesFromFiles_ExceptionBecauseFileNotExists() throws Exception {
        Collection<String> filenames = Collections.singletonList(
                "src/test/resources/not-exists.properties"
        );
        PropertyFiles propertyFiles = new PropertyFiles(enforcerLogger, StandardCharsets.UTF_8);
        propertyFiles.readPropertiesFromFilesWithoutCount(filenames);
    }

    @Test
    public void readPropertiesFromFiles_EmptyFilesAreOK() throws Exception {
        Map<String, Integer> expected = new HashMap<>();
        Collection<String> filenames = Collections.singletonList(
                "src/test/resources/empty.properties"
        );
        PropertyFiles propertyFiles = new PropertyFiles(enforcerLogger, StandardCharsets.UTF_8);
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
        PropertyFiles propertyFiles = new PropertyFiles(enforcerLogger, StandardCharsets.UTF_8);
        Map<String, Integer> properties = propertyFiles.readPropertiesFromFilesWithCount(filenames);
        assertEquals("Read properties are as expected.", expected, properties);
    }

}
