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

//    /**
//     * Read usages by matching the property name in template.
//     * this is substituted with the property name.
//     *
//     * @param filenames  Collection of file names to search for property usage.
//     * @param templatesAndProperties  Collection of templates (regexp) to use for matching, and their equivalent property.
//     * @return Map of usages not found and their location (file,row), though location does not matter here.
//     */
//    @SuppressWarnings({"squid:S134"})
//    @Nonnull
//    Set<UsageLocation> readDefinedUsagesFromFiles(
//            @Nonnull final Collection<String> filenames,
//            //@Nonnull final Collection<String> properties,
//            @Nonnull final Map<String,String> templatesAndProperties,
//            @Nonnull Charset charset)
//            throws IOException {
//        final Set<UsageLocation> results = new HashSet<>();
//        final Map<Pattern,String> tplPatterns = new HashMap<>();
//        // Using Pattern.LITERAL, not very good. Should be fixed into something more sensible.
//        templatesAndProperties.forEach((tpl,property) -> tplPatterns.put(Pattern.compile(tpl, Pattern.LITERAL),property));
//        for (String filename : filenames) {
//            log.debug("Reading file '" + filename + "'.");
//            Collection<String> lines = FileSpecs.readAllLines(Paths.get(filename), charset);
//            tplPatterns.forEach((tplP, property) -> {
//                log.debug("    Matching pattern '" + tplP.pattern() + "'.");
//                int rowNr = 1;
//                for (String row : lines) {
//                    log.debug("        Matching with row '" + row + "'.");
//                    Matcher matcher = tplP.matcher(row);
//                    if (matcher.find()) {
//                        log.debug("        Pattern match found (" + filename + ":" + rowNr + ")" + ", pattern '" + tplP.pattern() + "'.");
//                        results.add(new UsageLocation(property, rowNr, filename));
//                    }
//                    rowNr += 1;
//                }
//            });
//        }
//        return results;
//    }
//
    /**
     * Read usages by matching the property name in template.
     * this is substituted with the property name.
     *
     * @param filenames  Collection of file names to search for property usage.
     * @param templatesAndProperties  Collection of templates (regexp) to use for matching, and their equivalent property.
     * @return Map of usages not found and their location (file,row), though location does not matter here.
     */
    @SuppressWarnings({"squid:S134"})
    @Nonnull
    Set<String> readDefinedUsagesFromFiles(
            @Nonnull final Collection<String> filenames,
            @Nonnull final Map<String,String> templatesAndProperties,
            @Nonnull Charset charset)
            throws IOException {
        final Set<String> results = new HashSet<>();
        final Map<Pattern,String> tplPatterns = new HashMap<>();
        templatesAndProperties.forEach((tpl,property) -> tplPatterns.put(Pattern.compile(tpl, Pattern.COMMENTS | Pattern.UNICODE_CHARACTER_CLASS),property));
        for (String filename : filenames) {
            log.debug("Reading file '" + filename + "'.");
            String allFile = String.join("", Files.readAllLines(Paths.get(filename), charset));
            tplPatterns.forEach((tplP, property) -> {
                log.debug("    Matching pattern '" + tplP.pattern() + "'.");
                log.debug("        Matching with allFile '" + allFile + "'.");
                Matcher matcher = tplP.matcher(allFile);
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
        for (String filename : filenames) {
            log.debug("Reading file '" + filename + "'.");
            Collection<String> lines = Files.readAllLines(Paths.get(filename), charset);
            tplPatterns.forEach(tplP -> {
                log.debug("    Matching pattern '" + tplP.pattern() + "'.");
                int rowNr = 1;
                for (String row : lines) {
                    log.debug("        Matching with row '" + row + "'.");
                    Matcher matcher = tplP.matcher(row);
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

    class UsageLocation {
        private String property;
        private int row;
        private String filename;

        /**
         * @param property Property name
         * @param row      number
         * @param filename Name of file
         */
        UsageLocation(@Nonnull final String property, final int row, final String filename) {
            this.property = property;
            this.row = row;
            this.filename = filename;
        }

        @Nonnull
        public String getProperty() {
            return property;
        }

        public int getRow() {
            return row;
        }

        @Nonnull
        public String getFilename() {
            return filename;
        }
    }
}
