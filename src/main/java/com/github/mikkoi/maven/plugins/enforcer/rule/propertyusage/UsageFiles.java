package com.github.mikkoi.maven.plugins.enforcer.rule.propertyusage;

import org.apache.maven.plugin.logging.Log;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Handle issues with .properties files.
 */
class UsageFiles {

    private final Log log;

    UsageFiles(final Log log) {
        this.log = log;
    }

    /**
     * Read usages by matching the property name in template.
     * this is substituted with the property name.
     *
     * @param filenames  Collection of file names to search for property usage.
     * @param templatesAndProperties  Collection of templates (regexp) to use for matching, and their equivalent property.
     * @return Map of usages not found and their location (file,row), though location does not matter here.
     */
    @Nonnull
    Set<String> readDefinedUsagesFromFiles(
            @Nonnull final Collection<String> filenames,
            @Nonnull final Map<String,String> templatesAndProperties,
            @Nonnull final Charset charset)
            throws IOException {
        final Set<String> results = new HashSet<>();
        final Map<Pattern,String> tplPatterns = new HashMap<>();
        templatesAndProperties.forEach((tpl,property) -> tplPatterns.put(Pattern.compile(tpl, Pattern.COMMENTS | Pattern.UNICODE_CHARACTER_CLASS),property));
        for (final String filename : filenames) {
            log.debug("Reading file '" + filename + "'.");
            final String allFile = String.join("", Files.readAllLines(Paths.get(filename), charset));
            tplPatterns.forEach((tplP, property) -> {
                log.debug("    Matching pattern '" + tplP.pattern() + "'.");
                log.debug("        Matching with allFile '" + allFile + "'.");
                final Matcher matcher = tplP.matcher(allFile);
                if (matcher.find()) {
                    log.debug("        Pattern match found (" + filename + ")" + ", pattern '" + tplP.pattern() + "'.");
                    results.add(property);
                }
            });
        }
        return results;
    }

    /**
     * @param filenames Collection of file names to search for property usage.
     * @param templates Map of templates (regexp) to use for matching,
     *                  can be used to separate proper usage patterns for properties.
     * @return Map of usages not found and their location (file, row).
     */
    @Nonnull
    Set<UsageLocation> readAllUsagesFromFiles(
            @Nonnull final Collection<String> filenames,
            @Nonnull final Set<String> templates,
            @Nonnull final Charset charset)
            throws IOException {
        final Set<UsageLocation> foundProperties = new HashSet<>();
        final ArrayList<Pattern> tplPatterns = new ArrayList<>();
        templates.forEach(tpl -> tplPatterns.add(Pattern.compile(tpl, Pattern.COMMENTS | Pattern.UNICODE_CHARACTER_CLASS)));
        for (final String filename : filenames) {
            log.debug("Reading file '" + filename + "'.");
            final Collection<String> lines = Files.readAllLines(Paths.get(filename), charset);
            tplPatterns.forEach(tplP -> {
                log.debug("    Matching pattern '" + tplP.pattern() + "'.");
                int rowNr = 1;
                for (final String row : lines) {
                    log.debug("        Matching with row '" + row + "'.");
                    final Matcher matcher = tplP.matcher(row);
                    if (matcher.find()) {
                        log.debug("            Pattern match found (" + filename + ":" + rowNr + ")" + ", pattern '" + tplP.pattern() + "'.");
                        final String propname = matcher.group(1);
                        log.debug("            Extracted property '" + propname + "'.");
                        foundProperties.add(new UsageLocation(propname, rowNr, filename));
                    }
                    rowNr += 1;
                }
            });
        }
        return foundProperties;
    }

    static class UsageLocation {

        @Nonnull
        private String property;

        private int row;

        @Nonnull
        private String filename;

        /**
         * @param propertyVal Property name
         * @param rowVal      number
         * @param filenameVal Name of file
         */
        UsageLocation(@Nonnull final String propertyVal, final int rowVal, @Nonnull final String filenameVal) {
            property = propertyVal;
            row = rowVal;
            filename = filenameVal;
        }

        @Nonnull
        public String getProperty() {
            return property;
        }

        int getRow() {
            return row;
        }

        @Nonnull
        String getFilename() {
            return filename;
        }
    }
}
