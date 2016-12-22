package com.github.mikkoi.maven.plugins.enforcer.rule.propertyusage;

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

    private PropertyFiles() {}

    /**
     * @param filenames Collection of file names to read properties from.
     * @return Map of definitions and how many times they are defined.
     */
    @SuppressWarnings("squid:S134")
    @Nonnull
    static Map<String, Integer> readPropertiesFromFiles(@Nonnull final Collection<String> filenames)
            throws IOException {
        Map<String, Integer> results = new HashMap<>();
        for (String filename : filenames) {
            File file = new File(filename);
            Properties properties = new Properties();
            try (InputStream inputStream = new FileInputStream(file)) {
                properties.load(inputStream);
                for (String name : properties.stringPropertyNames()) {
                    if (!results.containsKey(name)) {
                        results.put(name, 1);
                    } else {
                        results.replace(name, results.get(name) + 1);
                    }
                }
            }
        }
        return results;
    }

}
