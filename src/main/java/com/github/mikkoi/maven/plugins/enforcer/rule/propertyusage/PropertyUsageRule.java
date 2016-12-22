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

import com.github.mikkoi.maven.plugins.enforcer.rule.propertyusage.configuration.Definitions;
import com.github.mikkoi.maven.plugins.enforcer.rule.propertyusage.configuration.Files;
import com.github.mikkoi.maven.plugins.enforcer.rule.propertyusage.configuration.Template;
import com.github.mikkoi.maven.plugins.enforcer.rule.propertyusage.configuration.Templates;
import com.github.mikkoi.maven.plugins.enforcer.rule.propertyusage.configuration.Usages;
import org.apache.maven.enforcer.rule.api.EnforcerRule;
import org.apache.maven.enforcer.rule.api.EnforcerRuleException;
import org.apache.maven.enforcer.rule.api.EnforcerRuleHelper;
import org.apache.maven.plugin.logging.Log;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;

/**
 * Verifies for usage of properties mentioned in .properties files.
 */
@SuppressWarnings("WeakerAccess")
public final class PropertyUsageRule implements EnforcerRule {

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

    /**
     * Properties which were defined more than once.
     */
    @Nonnull
    private final Set<String> propertiesDefinedMoreThanOnce = Collections.emptySet();

    /**
     * Logger given by Maven Enforcer.
     */
    Log log = null;

    /**
     * Definitions
     */
    @Nonnull
    private Collection<Collection<String>> definitions = Definitions.DEFAULT;

    /**
     * Templates
     */
    @Nonnull
    private Collection<Template> templates = Templates.DEFAULT;

    /**
     * Usages
     */
    @Nonnull
    private Collection<Collection<String>> usages = Usages.DEFAULT;

    @Nonnull
    public Set<String> getPropertiesDefinedMoreThanOnce() {
        return propertiesDefinedMoreThanOnce;
    }

    /**
     * @param helper EnforcerRuleHelper
     * @throws EnforcerRuleException Throws when error
     */
    @Override
    public void execute(@Nonnull final EnforcerRuleHelper helper)
            throws EnforcerRuleException {
        log = helper.getLog();

        // Get all the files to read for the properties definitions.

        Files fileSpecs = new Files(log);
        //Collection<String> filenames = fileSpecs.getAbsoluteFilenames();
        //filenames = filenames.stream().sorted().collect(Collectors.toSet());

        // Get the definitions and how many times they are defined.
        //HashMap<String, Integer> propsDefs =

                /*
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
        */
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

    @Nonnull
    public Set<String> getPropertiesNotUsed() {
        return propertiesNotUsed;
    }

    @Nonnull
    public Set<String> getPropertiesNotDefined() {
        return propertiesNotDefined;
    }

    public void setDefinitions(@Nonnull final Collection<Collection<String>> definitions) {
        this.definitions = definitions;
    }

    public void setTemplates(@Nonnull final Collection<Template> templates) {
        this.templates = templates;
    }

    public void setUsages(@Nonnull Collection<Collection<String>> usages) {
        this.usages = usages;
    }

}
