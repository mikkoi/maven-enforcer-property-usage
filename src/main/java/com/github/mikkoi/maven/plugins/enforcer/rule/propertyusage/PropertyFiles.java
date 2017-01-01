package com.github.mikkoi.maven.plugins.enforcer.rule.propertyusage;

import org.apache.maven.plugin.logging.Log;

import javax.annotation.Nonnull;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Handle issues with .properties files.
 */
class PropertyFiles {

    private final Log log;

    private final Charset charset;

    PropertyFiles(@Nonnull final Log logger, @Nonnull final Charset cset) {
        log = logger;
        charset = cset;
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
        Pattern commentLineP = Pattern.compile("^[\\s]{0,}[\\#\\!]{1}.{0,}$");
        log.debug("commentLineP:" + commentLineP.pattern());
        Pattern simplePropertyLineP = Pattern.compile("^[\\s]{0,}([^=:]{1,})[=:]{1}.{0,}$", Pattern.COMMENTS | Pattern.UNICODE_CHARACTER_CLASS);
        log.debug("simplePropertyLineP:" + simplePropertyLineP.pattern());
        Pattern notSimplePropertyLineP = Pattern.compile("^[\\s]{0,}([\\S]{1,})[\\s]{0,}.{0,}$", Pattern.COMMENTS | Pattern.UNICODE_CHARACTER_CLASS);
        log.debug("simplePropertyLineP:" + simplePropertyLineP.pattern());
        Pattern multiLineP = Pattern.compile("\\\\{1}[\\s]{0,}$");
        log.debug("multiLineP:" + multiLineP);
        for (String filename : filenames) {
            log.debug("Reading property file '" + filename + "'.");
            List<String> rows = Files.readAllLines(Paths.get(filename), charset);
            boolean readingMultiLineDefinition = false;
            for ( String row : rows) {
                log.debug("    Reading property row '" + row + "'.");
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
                    final String name = simplePropertyLineM.group(1).trim();
                    storePropertyName(name, results);
                    continue;
                }
                Matcher notSimplePropertyLineM = notSimplePropertyLineP.matcher(row);
                if(notSimplePropertyLineM.find()) {
                    log.debug("        This is not simple property line.");
                    final String name = notSimplePropertyLineM.group(1).trim();
                    storePropertyName(name, results);
                    continue;
                }
                log.debug("        This row matched nothing,  propably empty or multiline continuation.");
            }
        }
        return results;
    }

    private void storePropertyName(@Nonnull final String name, @Nonnull Map<String,Integer> properties) {
        log.debug("            Reading property " + name + ".");
        if (!properties.containsKey(name)) {
            log.debug("            Not defined before.");
            properties.put(name, 1);
        } else {
            log.debug("            Defined before. Update counter.");
            properties.replace(name, properties.get(name) + 1);
        }
    }
}
