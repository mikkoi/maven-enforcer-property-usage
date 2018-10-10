package com.github.mikkoi.maven.plugins.enforcer.rule.propertyusage;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Nonnull;

import org.apache.maven.plugin.logging.Log;

/**
 * Handle issues with .properties files.
 */
class PropertyFiles {

    private final Log log;

    private final Charset charset;

    private final Pattern commentLineP = Pattern.compile("^[\\s]{0,}[\\#\\!]{1}.{0,}$");
    private final Pattern simplePropertyLineP = Pattern.compile("^[\\s]{0,}([^=:]{1,})[=:]{1}(.{0,})$", Pattern.COMMENTS | Pattern.UNICODE_CHARACTER_CLASS);
    private final Pattern notSimplePropertyLineP = Pattern.compile("^[\\s]{0,}([\\S]{1,})[\\s]{0,}(.{0,})$", Pattern.COMMENTS | Pattern.UNICODE_CHARACTER_CLASS);
    private final Pattern multiLineP = Pattern.compile("\\\\{1}[\\s]{0,}$");

    PropertyFiles(@Nonnull final Log logger, @Nonnull final Charset cset) {
        log = logger;
        charset = cset;
    }

    /**
     * @param filenames Collection of file names to read properties from.
     * @return Map of definitions and how many times they are defined.
     */
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
     * Read properties with our own reading routine and count
     * how many times they are used.
     *
     * @param filenames Collection of file names to read properties from.
     * @return Map of definitions and how many times they are defined.
     */
    @Nonnull
    Map<String, Integer> readPropertiesFromFilesWithCount(@Nonnull final Collection<String> filenames)
            throws IOException {
        final Map<String, Integer> results = new HashMap<>();
        log.debug("commentLineP:" + commentLineP.pattern());
        log.debug("simplePropertyLineP:" + simplePropertyLineP.pattern());
        log.debug("simplePropertyLineP:" + simplePropertyLineP.pattern());
        log.debug("multiLineP:" + multiLineP);
        for (final String filename : filenames) {
            log.debug("Reading property file '" + filename + "'.");
            readPropertiesFromFileWithCount(filename).forEach((key,value) -> results.put(key, value));
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
    @Nonnull
    Map<String, Integer> readPropertiesFromFileWithCount(@Nonnull final String filename)
            throws IOException {
        final Map<String, Integer> results = new HashMap<>();
        final Map<String, Set<PropertyDefinition>> propertyDefinitions = new HashMap<>();
        List<String> rows = Files.readAllLines(Paths.get(filename), charset);
        boolean readingMultiLineDefinition = false;
        int linenumber = 0;
        for ( String row : rows) {
        	linenumber++;
            log.debug("    Reading property row '" + row + "' (" + linenumber + ").");
            Matcher commentLineM = commentLineP.matcher(row);
            if (commentLineM.find()) {
                log.debug("        This is comment line.");
                continue;
            }
            Matcher multiLineM = multiLineP.matcher(row);
            if (multiLineM.find()) {
                if( readingMultiLineDefinition) {
                    log.debug("        This is multirow (not first row)");
                    continue;
                } else {
                    log.debug("        This is multirow (first row).");
                    readingMultiLineDefinition = true;
                }
            } else {
                if( readingMultiLineDefinition) {
                    log.debug("        This is multirow (last row).");
                    readingMultiLineDefinition = false;
                    continue;
                }
            }
            Matcher simplePropertyLineM = simplePropertyLineP.matcher(row);
            if (simplePropertyLineM.find()) {
                log.debug("        This is simple property line.");
                final String key = simplePropertyLineM.group(1).trim();
                final String value = simplePropertyLineM.group(2).trim();
                storePropertyName(key, value, filename, linenumber, results, propertyDefinitions);
                continue;
            }
            Matcher notSimplePropertyLineM = notSimplePropertyLineP.matcher(row);
            if(notSimplePropertyLineM.find()) {
                log.debug("        This is not simple property line.");
                final String key = notSimplePropertyLineM.group(1).trim();
                final String value = simplePropertyLineM.group(2).trim();
                storePropertyName(key, value, filename, linenumber, results, propertyDefinitions);
                continue;
            }
            log.debug("        This row matched nothing,  propably empty or multiline continuation.");
        }
        return results;
    }

    private void storePropertyName(@Nonnull final String key, @Nonnull final String value, @Nonnull final String filename, final int linenumber, @Nonnull final Map<String,Integer> properties, @Nonnull final Map<String,Set<PropertyDefinition>> propertyDefinitions) {
        log.debug("            Reading property " + key + ".");
        if (!properties.containsKey(key)) {
            log.debug("            Not defined before.");
            properties.put(key, 1);
            final Set<PropertyDefinition> p = new HashSet<>();
            p.add(new PropertyDefinition(key, value, filename, linenumber));
            propertyDefinitions.put(key, p);
        } else {
            log.debug("            Defined before. Update counter.");
            properties.replace(key, properties.get(key) + 1);
            propertyDefinitions.get(key).add(new PropertyDefinition(key, value, filename, linenumber));
        }
    }
}
