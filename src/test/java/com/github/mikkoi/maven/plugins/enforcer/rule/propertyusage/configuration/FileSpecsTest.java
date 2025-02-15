package com.github.mikkoi.maven.plugins.enforcer.rule.propertyusage.configuration;

import org.apache.maven.enforcer.rule.api.EnforcerLevel;
import org.apache.maven.enforcer.rule.api.EnforcerLogger;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.plugin.logging.SystemStreamLog;
import org.codehaus.plexus.configuration.PlexusConfiguration;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.nio.file.Paths;
import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static com.github.mikkoi.maven.plugins.enforcer.rule.propertyusage.configuration.FileSpecs.absoluteCwdAndFile;
import static org.junit.Assert.assertEquals;

public class FileSpecsTest {

    @Rule
    public TemporaryFolder testDir = new TemporaryFolder();

    public EnforcerLogger enforcerLogger = new EnforcerLogger() {
        protected final Log log = new SystemStreamLog();
//            protected final Log log;
//
//            protected EnforcerLogger(Log log) {
//                this.log = Objects.requireNonNull(log, "log must be not null");
//            }

        @Override
        public void warnOrError(CharSequence message) {

        }

        @Override
        public void warnOrError(Supplier<CharSequence> messageSupplier) {

        }

        @Override
        public boolean isDebugEnabled() {
            return log.isDebugEnabled();
        }

        @Override
        public void debug(CharSequence message) {
//            log.debug(message);
        }

        @Override
        public void debug(Supplier<CharSequence> messageSupplier) {
            if (log.isDebugEnabled()) {
                log.debug(messageSupplier.get());
            }
        }

        @Override
        public boolean isInfoEnabled() {
            return log.isInfoEnabled();
        }

        @Override
        public void info(CharSequence message) {
//            log.info(message);
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
    public void getFilenamesFromFileSpecs() throws Exception {
        SystemStreamLog slog = new SystemStreamLog();

//        EnforcerLogger enforcerLoggerError = new EnforcerLoggerError(log);
//        EnforcerLogger enforcerLoggerWarn = new EnforcerLoggerWarn(log);
//
//        for (PlexusConfiguration ruleConfig : rules.getChildren()) {
//            // we need rule level before configuration in order to proper set logger
//            EnforcerLevel ruleLevel = getRuleLevelFromConfig(ruleConfig);
//
//            EnforcerRuleDesc ruleDesc = createRuleDesc(ruleConfig.getName(), ruleConfig.getAttribute("implementation"));
//            // setup logger before rule configuration
//            ruleDesc.getRule().setLog(ruleLevel == EnforcerLevel.ERROR ? enforcerLoggerError : enforcerLoggerWarn);




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
//        SystemStreamLog slog = new SystemStreamLog();
        Collection<String> files = Arrays.asList(
                absoluteCwdAndFile("src/test/resources/no-exist-dir/*.properties"),
                absoluteCwdAndFile("src/test/resources/no-way.properties")
        );
        Collection<String> expected = Collections.emptySet();
//        expected = expected.stream().sorted().collect(Collectors.toSet());
//        Collection<String> filenames = FileSpecs.getAbsoluteFilenames(files, Paths.get("").toAbsolutePath(), enforcerLogger);
//        filenames = filenames.stream().sorted().collect(Collectors.toSet());
        Collection<String> filenames = new HashSet<>(FileSpecs
                .getAbsoluteFilenames(files, Paths.get("").toAbsolutePath(), enforcerLogger));
        assertEquals("Filenames are as expected (none).", expected, filenames);
    }
}
