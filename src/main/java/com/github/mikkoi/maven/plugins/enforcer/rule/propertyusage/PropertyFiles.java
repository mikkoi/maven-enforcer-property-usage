package com.github.mikkoi.maven.plugins.enforcer.rule.propertyusage;

import org.apache.maven.enforcer.rule.api.EnforcerLogger;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.regex.qual.Regex;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Handle issues with .properties files.
 */
class PropertyFiles {

    private final EnforcerLogger log;

    private final Charset charset;

    private final Pattern commentLineP = Pattern.compile("^[\\s]{0,}[\\#\\!]{1}.{0,}$");
    private final Pattern simplePropertyLineP = Pattern.compile("^[\\s]{0,}([^=:]{1,})[=:]{1}(.{0,})$", Pattern.COMMENTS | Pattern.UNICODE_CHARACTER_CLASS);
    private final Pattern notSimplePropertyLineP = Pattern.compile("^[\\s]{0,}([\\S]{1,})[\\s]{0,}(.{0,})$", Pattern.COMMENTS | Pattern.UNICODE_CHARACTER_CLASS);
    private final Pattern multiLineP = Pattern.compile("\\\\{1}[\\s]{0,}$");

    PropertyFiles(final @NonNull EnforcerLogger logger, final @NonNull Charset cset) {
        log = logger;
        charset = cset;
    }

    /**
     * @param filenames Collection of file names to read properties from.
     * @return Map of definitions and how many times they are defined.
     */
    @NonNull Map<String, Integer> readPropertiesFromFilesWithoutCount(final @NonNull Collection<String> filenames)
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
     * Read properties with our own reading routine and count
     * how many times they are used.
     *
     * @param filenames Collection of file names to read properties from.
     * @return Map of definitions and how many times they are defined.
     */
    @NonNull
    Map<String, Integer> readPropertiesFromFilesWithCount(final @NonNull Collection<String> filenames)
            throws IOException {
        final Map<String, Integer> results = new HashMap<>();
        log.debug("commentLineP:" + commentLineP.pattern());
        log.debug("simplePropertyLineP:" + simplePropertyLineP.pattern());
        log.debug("simplePropertyLineP:" + simplePropertyLineP.pattern());
        log.debug("multiLineP:" + multiLineP);
        for (final String filename : filenames) {
            log.debug("Reading property file '" + filename + "'.");
            readPropertiesFromFileWithCount(filename).forEach((key, value) -> results.put(key, value));
        }
        return results;
    }

    /**
     * Read properties with our own reading routine and return
     * a set of definition instances
     *
     * @param filenames Collection of file names to read properties from.
     * @return Map of definitions and PropertyDefinitions
     */
    @NonNull Map<String, Set<PropertyDefinition>> readPropertiesFromFilesGetDefinitions(final @NonNull Collection<String> filenames)
            throws IOException {
        final Map<String, Set<PropertyDefinition>> results = new HashMap<>();
        log.debug("commentLineP:" + commentLineP.pattern());
        log.debug("simplePropertyLineP:" + simplePropertyLineP.pattern());
        log.debug("simplePropertyLineP:" + simplePropertyLineP.pattern());
        log.debug("multiLineP:" + multiLineP);
        for (final String filename : filenames) {
            log.debug("Reading property file '" + filename + "'.");
            readPropertiesFromFileGetDefinitions(filename).forEach((key, value) -> {
                log.debug("key:" + key);
                log.debug("value:" + value);
                if(results.containsKey(key)) {
                    results.get(key).addAll(value);
                } else {
                    results.put(key, value);
                }
            });
        }
        return results;
    }

    /**
     * Read properties with our own reading routine and count
     * how many times they are used.
     *
     * @param filename File name to read properties from.
     * @return Map of definitions and how many times they are defined.
     */
    @SuppressWarnings({
            "squid:S3776", // Cognitive Complexity of methods should not be too high
            "squid:S134",  // Control flow statements "if", "for", "while", "switch" and "try" should not be nested too deeply
            "squid:S135"   // Loops should not contain more than a single "break" or "continue" statement
    })
    @NonNull Map<String, Integer> readPropertiesFromFileWithCount(final @NonNull String filename)
            throws IOException {
        final Map<String, Integer> results = new HashMap<>();
        List<String> rows = Files.readAllLines(Paths.get(filename), charset);
        boolean readingMultiLineDefinition = false;
        int linenumber = 0;
        for (String row : rows) {
            linenumber++;
            log.debug("    Reading property row '" + row + "' (" + linenumber + ").");
            final @Regex(0) Matcher commentLineM = commentLineP.matcher(row);
            if (commentLineM.find()) {
                log.debug("        This is comment line.");
                continue;
            }
            final @Regex(0) Matcher multiLineM = multiLineP.matcher(row);
            if (multiLineM.find()) {
                if (readingMultiLineDefinition) {
                    log.debug("        This is multirow (not first row)");
                    continue;
                } else {
                    log.debug("        This is multirow (first row).");
                    readingMultiLineDefinition = true;
                }
            } else {
                if (readingMultiLineDefinition) {
                    log.debug("        This is multirow (last row).");
                    readingMultiLineDefinition = false;
                    continue;
                }
            }
            final @Regex(1) Matcher simplePropertyLineM = simplePropertyLineP.matcher(row);
            if (simplePropertyLineM.find()) {
                log.debug("        This is simple property line.");
                @SuppressWarnings("nullness")
                final String key = simplePropertyLineM.group(1).trim();
                storePropertyName(key, results);
                continue;
            }
            final @Regex(1) Matcher notSimplePropertyLineM = notSimplePropertyLineP.matcher(row);
            if (notSimplePropertyLineM.find()) {
                log.debug("        This is not simple property line.");
                @SuppressWarnings("nullness")
                final String key = notSimplePropertyLineM.group(1).trim();
                storePropertyName(key, results);
                continue;
            }
            log.debug("        This row matched nothing,  propably empty or multiline continuation.");
        }
        return results;
    }

    /**
     * Read properties with our own reading routine ...
     *
     * @param filename File name to read properties from.
     * @return Map of definitions and ...
     */
    @SuppressWarnings({
            "squid:S3776", // Cognitive Complexity of methods should not be too high
            "squid:S134",  // Control flow statements "if", "for", "while", "switch" and "try" should not be nested too deeply
            "squid:S135"   // Loops should not contain more than a single "break" or "continue" statement
    })
    @NonNull Map<String, Set<PropertyDefinition>> readPropertiesFromFileGetDefinitions(final @NonNull String filename)
            throws IOException {
        final Map<String, Set<PropertyDefinition>> propertyDefinitions = new HashMap<>();
        List<String> rows = Files.readAllLines(Paths.get(filename), charset);
        boolean readingMultiLineDefinition = false;
        int linenumber = 0;
        for (String row : rows) {
            linenumber++;
            log.debug("    Reading property row '" + row + "' (" + linenumber + ").");
            final @Regex(2) Matcher commentLineM = commentLineP.matcher(row);
            if (commentLineM.find()) {
                log.debug("        This is comment line.");
                continue;
            }
            final @Regex(0) Matcher multiLineM = multiLineP.matcher(row);
            if (multiLineM.find()) {
                if (readingMultiLineDefinition) {
                    log.debug("        This is multirow (not first row)");
                    continue;
                } else {
                    log.debug("        This is multirow (first row).");
                    readingMultiLineDefinition = true;
                }
            } else {
                if (readingMultiLineDefinition) {
                    log.debug("        This is multirow (last row).");
                    readingMultiLineDefinition = false;
                    continue;
                }
            }
            final @Regex(2) Matcher simplePropertyLineM = simplePropertyLineP.matcher(row);
            if (simplePropertyLineM.find()) {
                log.debug("        This is simple property line.");
                @SuppressWarnings("nullness")
                final String key = simplePropertyLineM.group(1).trim();
                @SuppressWarnings("nullness")
                final String value = simplePropertyLineM.group(2).trim();
                storePropertyDefinition(key, value, filename, linenumber, propertyDefinitions);
                continue;
            }
            final @Regex(2) Matcher notSimplePropertyLineM = notSimplePropertyLineP.matcher(row);
            if (notSimplePropertyLineM.find()) {
                log.debug("        This is not simple property line.");
                @SuppressWarnings("nullness")
                final String key = Objects.requireNonNull(notSimplePropertyLineM.group(1)).trim();
                @SuppressWarnings("nullness")
                final String value = Objects.requireNonNull(notSimplePropertyLineM.group(2)).trim();
                storePropertyDefinition(key, value, filename, linenumber, propertyDefinitions);
                continue;
            }
            log.debug("        This row matched nothing,  propably empty or multiline continuation.");
        }
        return propertyDefinitions;
    }

    private void storePropertyName(final @NonNull String key, final @NonNull Map<String, Integer> properties) {
        log.debug("            Reading property " + key + ".");
        if (!properties.containsKey(key)) {
            log.debug("            Not defined before.");
            properties.put(key, 1);
        } else {
            log.debug("            Defined before. Update counter.");
            properties.replace(key, properties.get(key) + 1);
        }
    }

    private void storePropertyDefinition(final @NonNull String key, final @NonNull String value, final @NonNull String filename, final int linenumber, final @NonNull Map<String, Set<PropertyDefinition>> propertyDefinitions) {
        log.debug("            Reading property " + key + ".");
        if (propertyDefinitions.containsKey(key)) {
            log.debug("            Defined before.");
            propertyDefinitions.get(key).add(new PropertyDefinition(key, value, filename, linenumber));
        } else {
            log.debug("            Not defined before.");
            final Set<PropertyDefinition> defs = new HashSet<>();
            defs.add(new PropertyDefinition(key, value, filename, linenumber));
            propertyDefinitions.put(key, defs);
        }
    }
}
