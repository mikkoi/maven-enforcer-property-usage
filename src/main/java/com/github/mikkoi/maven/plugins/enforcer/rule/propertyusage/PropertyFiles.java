package com.github.mikkoi.maven.plugins.enforcer.rule.propertyusage;

import org.apache.maven.plugin.logging.Log;

import javax.annotation.Nonnull;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Handle issues with .properties files.
 */
class PropertyFiles {

    private final Log log;

    PropertyFiles(final Log log) {
        this.log = log;
    }

    /**
     * @param filenames Collection of file names to read properties from.
     * @return Map of definitions and how many times they are defined.
     */
    @SuppressWarnings("squid:S134")
    @Nonnull
    Map<String, Integer> readPropertiesFromFilesWithoutCount(@Nonnull final Collection<String> filenames)
            throws IOException {
        Map<String, Integer> results = new HashMap<>();
        for (String filename : filenames) {
            File file = new File(filename);
            Properties properties = new Properties();
            log.debug("PropertyFiles:readPropertiesFromFilesWithoutCount() Reading file " + filename + ".");
            try (InputStream inputStream = new FileInputStream(file)) {
                properties.load(inputStream);
                for (String name : properties.stringPropertyNames()) {
                    log.debug("    Reading property " + name + ".");
                    results.put(name, 1);
                }
            }
        }
        return results;
    }

    /**
     * TODO Fix this function.
     *
     * @param filenames Collection of file names to read properties from.
     * @return Map of definitions and how many times they are defined.
     */
    @SuppressWarnings("squid:S134")
    @Nonnull
    Map<String, Integer> readPropertiesFromFilesWithCount(@Nonnull final Collection<String> filenames)
            throws IOException {
        Map<String, Integer> results = new HashMap<>();
        for (String filename : filenames) {
            File file = new File(filename);
            Properties properties = new Properties();
            log.debug("PropertyFiles:readPropertiesFromFilesWithCount() Reading file " + filename + ".");
            try (InputStream inputStream = new FileInputStream(file)) {
                properties.load(inputStream);
                for (String name : properties.stringPropertyNames()) {
                    log.debug("    Reading property " + name + ".");
                    if (!results.containsKey(name)) {
                        log.debug("        Not defined before.");
                        results.put(name, 1);
                    } else {
                        log.debug("        Defined before. Update counter.");
                        results.replace(name, results.get(name) + 1);
                    }
                }
            }
        }
        return results;
    }

}
