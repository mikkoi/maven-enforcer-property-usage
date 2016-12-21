package com.github.mikkoi.maven.plugins.enforcer.rule.propertyusage;

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import com.github.mikkoi.maven.plugins.enforcer.rule.propertyusage.configuration.*;
import com.google.common.io.ByteStreams;
import com.sun.tools.doclets.internal.toolkit.util.DocFinder;
import org.apache.maven.enforcer.rule.api.EnforcerRule;
import org.apache.maven.enforcer.rule.api.EnforcerRuleException;
import org.apache.maven.enforcer.rule.api.EnforcerRuleHelper;
import org.apache.maven.plugin.logging.Log;
import org.codehaus.plexus.component.configurator.expression.ExpressionEvaluationException;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.charset.*;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;
import java.util.regex.Pattern;

/**
 * Verifies for usage of properties mentioned in .properties files.
 */
@SuppressWarnings("WeakerAccess")
public final class PropertyUsageRule implements EnforcerRule {

    Log log = null;
    @Nonnull
    private static final String INCLUDE_REGEX_DEFAULT = ".*";
    @Nonnull
    private static final String EXCLUDE_REGEX_DEFAULT = "";
    @Nonnull
    private static final String DIRECTORY_DEFAULT = "src";

    /**
     * Definitions
     */
    @Nonnull
    private Collection<Collection<FileSpec>> definitions = Definitions.DEFAULT;

    /**
     * Templates
     */
    @Nonnull
    private Collection<Template> templates = Templates.DEFAULT;

    /**
     * Usages
     */
    @Nonnull
    private Collection<Collection<FileSpec>> usages = Usages.DEFAULT;

    /**
     * Properties which were not found in usages.
     */
    @Nonnull
    private final Set<String> propertiesNotUsed = Collections.emptySet();

    /**
     * Properties which were used in usages but not defined in definitions.
     */
    @Nonnull
    private final Set<String> propertiesNotDefined = Collections.emptySet();

    ///////////////
    /**
     * Faulty files list. Can be accessed after processing execute().
     */
    @Nonnull
    private final Collection<FileResult> faultyFiles = new ArrayList<>();
    /**
     * Validate files must match this requireEncoding.
     * Default: ${project.builder.sourceEncoding}.
     */
    @Nullable
    private String requireEncoding = null;
    /**
     * Directory to search for files.
     */
    @Nullable
    private String directory = null;
    /**
     * Regular Expression to match file names against for filtering in.
     */
    @Nullable
    private String includeRegex = null;
    /**
     * Regular Expression to match file names against for filtering out
     * Can be used together with includeRegex.
     * includeRegex will first pick files in,
     * then excludeRegex will filter out files from the selected ones.
     */
    @Nullable
    private String excludeRegex = null;

    /**
     * Get the faulty files list.
     */
    @Nonnull
    public Collection<FileResult> getFaultyFiles() {
        return faultyFiles;
    }

    /**
     * @param helper EnforcerRuleHelper
     * @throws EnforcerRuleException Throws when error
     */
    @Override
    public void execute(@Nonnull final EnforcerRuleHelper helper)
            throws EnforcerRuleException {
        log = helper.getLog();

        try {
            // get the various expressions out of the helper.
            String basedir = helper.evaluate("${project.basedir}").toString();

            log.debug("Retrieved Basedir: " + basedir);
            log.debug("requireEncoding: " + (requireEncoding == null ? "null" : requireEncoding));
            log.debug("directory: " + (directory == null ? "null" : directory));

//            log.debug("requireEncoding: " + this.getRequireEncoding());
//            log.debug("directory: " + this.getDirectory());
//            log.debug("includeRegex: " + this.getIncludeRegex());
//            log.debug("excludeRegex: " + this.getExcludeRegex());

            // Check the existence of the wanted directory:
            final Path dir = Paths.get(basedir, getDirectory());
            log.debug("Get files in dir '" + dir.toString() + "'.");
            if (!dir.toFile().exists()) {
                throw new EnforcerRuleException(
                        "Directory '" + dir.toString() + "' not found."
                                + " Specified by parameter 'directory' (value: '" + this.getDirectory() + "')!"
                );
            }

            // Put all files into this collection:
            Collection<FileResult> allFiles = getFileResults(log, dir);

            // Copy faulty files to another list.
            log.debug("Moving possible faulty files (faulty encoding) to another list.");
            for (FileResult res : allFiles) {
                log.debug("Checking if file '" + res.getPath().toString() + "' has encoding '" + requireEncoding + "'.");
                boolean hasCorrectEncoding = true;
                try (FileInputStream fileInputStream = new FileInputStream(res.getPath().toFile())) {
                    byte[] bytes = ByteStreams.toByteArray(fileInputStream);
                    Charset charset = Charset.forName(this.getRequireEncoding());
                    CharsetDecoder decoder = charset.newDecoder();
                    ByteBuffer byteBuffer = ByteBuffer.wrap(bytes);
                    decoder.decode(byteBuffer);
                } catch (CharacterCodingException e) {
                    hasCorrectEncoding = false;
                } catch (IOException e) {
                    e.printStackTrace();
                    log.error(e.getMessage());
                    hasCorrectEncoding = false;
                }
                if (!hasCorrectEncoding) {
                    log.debug("Moving faulty file: " + res.getPath());
                    FileResult faultyFile = new FileResult.Builder(res.getPath())
                            .lastModified(res.getLastModified())
                            .build();
                    faultyFiles.add(faultyFile);
                } else {
                    log.debug("Has correct encoding. Not moving to faulty files list.");
                }
            }
            log.debug("All faulty files moved.");

            // Report
            if (!faultyFiles.isEmpty()) {
                final StringBuilder builder = new StringBuilder();
                builder.append("Wrong encoding in following files:");
                builder.append(System.getProperty("line.separator"));
                for (FileResult res : faultyFiles) {
                    builder.append(res.getPath());
                    builder.append(System.getProperty("line.separator"));
                }
                throw new EnforcerRuleException(builder.toString());
            }
        } catch (ExpressionEvaluationException e) {
            throw new EnforcerRuleException(
                    "Unable to lookup an expression " + e.getLocalizedMessage(), e
            );
        }
    }

    @Nonnull
    private Collection<FileResult> getFileResults(final Log log, final Path dir) {
        Collection<FileResult> allFiles = new ArrayList<>();
        FileVisitor<Path> fileVisitor = new com.github.mikkoi.maven.plugins.enforcer.rule.propertyusage.PropertyUsageRule.GetEncodingsFileVisitor(
                log,
                this.getIncludeRegex() != null ? this.getIncludeRegex() : INCLUDE_REGEX_DEFAULT,
                this.getExcludeRegex() != null ? this.getExcludeRegex() : EXCLUDE_REGEX_DEFAULT,
                allFiles
        );
        try {
            Set<FileVisitOption> visitOptions = new LinkedHashSet<>();
            visitOptions.add(FileVisitOption.FOLLOW_LINKS);
            java.nio.file.Files.walkFileTree(dir,
                    visitOptions,
                    Integer.MAX_VALUE,
                    fileVisitor
            );
        } catch (Exception e) {
            log.error(e.getCause() + e.getMessage());
        }
        return allFiles;
    }

    /**
     * If your rule is cacheable, you must return a unique id when parameters or conditions
     * change that would cause the result to be different. Multiple cached results are stored
     * based on their id.
     * <p>
     * The easiest way to do this is to return a hash computed from the values of your parameters.
     * <p>
     * If your rule is not cacheable, then the result here is not important, you may return anything.
     *
     * @return Always false here.
     */
    @Override
    @Nullable
    public String getCacheId() {
        return String.valueOf(false);
    }

    /**
     * This tells the system if the results are cacheable at all. Keep in mind that during
     * forked builds and other things, a given rule may be executed more than once for the same
     * project. This means that even things that change from project to project may still
     * be cacheable in certain instances.
     *
     * @return Always false here.
     */
    @Override
    public boolean isCacheable() {
        return false;
    }

    /**
     * If the rule is cacheable and the same id is found in the cache, the stored results
     * are passed to this method to allow double checking of the results. Most of the time
     * this can be done by generating unique ids, but sometimes the results of objects returned
     * by the helper need to be queried. You may for example, store certain objects in your rule
     * and then query them later.
     *
     * @param arg0 EnforcerRule
     * @return Always false here.
     */
    @Override
    public boolean isResultValid(@Nullable final EnforcerRule arg0) {
        return false;
    }

    /**
     * Getters and setters for the parameters (these are filled by Maven).
     */

    @SuppressWarnings("WeakerAccess")
    @Nullable
    public String getDirectory() {
        return directory;
    }

    @SuppressWarnings("WeakerAccess")
    public void setDirectory(@Nullable final String directory) {
        this.directory = directory;
    }

    @SuppressWarnings("WeakerAccess")
    @Nullable
    public String getIncludeRegex() {
        return includeRegex;
    }

    @SuppressWarnings("WeakerAccess")
    public void setIncludeRegex(@Nullable final String includeRegex) {
        this.includeRegex = includeRegex;
    }

    @SuppressWarnings("WeakerAccess")
    @Nullable
    public String getExcludeRegex() {
        return excludeRegex;
    }

    @SuppressWarnings("WeakerAccess")
    public void setExcludeRegex(@Nullable final String excludeRegex) {
        this.excludeRegex = excludeRegex;
    }

    @SuppressWarnings("WeakerAccess")
    @Nullable
    public String getRequireEncoding() {
        return requireEncoding;
    }

    @SuppressWarnings("WeakerAccess")
    public void setRequireEncoding(@Nullable final String requireEncoding) {
        this.requireEncoding = requireEncoding;
    }

    @Nonnull
    public Set<String> getPropertiesNotUsed() {
        return propertiesNotUsed;
    }

    @Nonnull
    public Set<String> getPropertiesNotDefined() {
        return propertiesNotDefined;
    }

    public void setDefinitions(@Nonnull final Collection<Collection<FileSpec>> definitions) {
        this.definitions = definitions;
    }

    public void setTemplates(@Nonnull final Collection<Template> templates) {
        this.templates = templates;
    }

    public void setUsages(@Nonnull Collection<Collection<FileSpec>> usages) {
        this.usages = usages;
    }

    /**
     * @param fileSpecs Collection of file specs to read properties from.
     * @return Map of definitions and how many times they are defined.
     */
    @Nonnull
    public static Map<String, Integer> readPropertiesFromFiles(@Nonnull final Collection<FileSpec> fileSpecs)
            throws IOException {
        Map<String, Integer> results = new HashMap<>();
        for (FileSpec fileSpec : fileSpecs) {
            File file = new File(fileSpec.getFile());
            Properties properties = new Properties();
            try (InputStream inputStream = new FileInputStream(file)) {
                properties.load(inputStream);
                Set<String> names = properties.stringPropertyNames();
                for (String name : names) {
                    if (!results.containsKey(name)) {
                        results.put(name, 0);
                    } else {
                        results.replace(name, results.get(name) + 1);
                    }
                }
            }
        }
        return results;
    }

    /**
     * Extended SimpleFileVisitor for walking through the files.
     */
    private static class GetEncodingsFileVisitor extends SimpleFileVisitor<Path> {
        @Nonnull
        private final Log log;
        private final boolean includeRegexUsed;
        @Nonnull
        private final Pattern includeRegexPattern;
        private final boolean excludeRegexUsed;
        @Nonnull
        private final Pattern excludeRegexPattern;
        @Nonnull
        private final Collection<FileResult> results;

        /**
         * Constructor.
         *
         * @param pluginLog    Maven Plugin logging channel.
         * @param includeRegex Include regex pattern.
         * @param excludeRegex Exclude regex pattern.
         * @param fileResults  Initialized collection to be filled.
         */
        GetEncodingsFileVisitor(
                @Nonnull final Log pluginLog,
                @Nonnull final String includeRegex,
                @Nonnull final String excludeRegex,
                @Nonnull final Collection<FileResult> fileResults
        ) {
            this.log = pluginLog;
            // Attn. Because we have includeRegex default (.*) which replaces
            // an empty includeRegex, includeRegex can never have length 0 chars!
            // But excludeRegex can have length 0 chars!
            includeRegexUsed = true;
            includeRegexPattern = Pattern.compile(includeRegex);
            if (excludeRegex.length() > 0) {
                excludeRegexUsed = true;
                excludeRegexPattern = Pattern.compile(excludeRegex);
            } else {
                excludeRegexUsed = false;
                excludeRegexPattern = Pattern.compile("");
            }
            this.results = fileResults;
        }

        @Override
        public FileVisitResult visitFile(
                final Path aFile, final BasicFileAttributes aAttrs
        ) throws IOException {
            log.debug("Visiting file '" + aFile.toString() + "'.");
            if (includeRegexUsed && !includeRegexPattern.matcher(aFile.toString()).find()) {
                log.debug("FileSpec not matches includeRegex in-filter. Exclude file from list!");
                return FileVisitResult.CONTINUE;
            }
            if (excludeRegexUsed && excludeRegexPattern.matcher(aFile.toString()).find()) {
                log.debug("FileSpec matches excludeRegex out-filter. Exclude file from list!");
                return FileVisitResult.CONTINUE;
            }
            log.debug("FileSpec matches includeRegex in-filter and not matches excludeRegex out-filter. Include file to list!");
            File file = aFile.toFile();
            FileResult res = new FileResult.Builder(aFile.toAbsolutePath())
                    .lastModified(file.lastModified())
                    .build();
            results.add(res);
            return FileVisitResult.CONTINUE;
        }

        @Override
        public FileVisitResult preVisitDirectory(
                final Path aDir, final BasicFileAttributes aAttrs
        ) throws IOException {
            log.debug("Visiting directory '" + aDir.toString() + "'.");
            return FileVisitResult.CONTINUE;
        }

    }

}
