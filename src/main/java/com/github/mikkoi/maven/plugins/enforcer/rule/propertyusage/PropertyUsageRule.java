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

import com.github.mikkoi.maven.plugins.enforcer.rule.propertyusage.UsageFiles.UsageLocation;
import com.github.mikkoi.maven.plugins.enforcer.rule.propertyusage.configuration.Definitions;
import com.github.mikkoi.maven.plugins.enforcer.rule.propertyusage.configuration.Files;
import com.github.mikkoi.maven.plugins.enforcer.rule.propertyusage.configuration.Templates;
import com.github.mikkoi.maven.plugins.enforcer.rule.propertyusage.configuration.Usages;
import org.apache.maven.enforcer.rule.api.EnforcerRule;
import org.apache.maven.enforcer.rule.api.EnforcerRuleException;
import org.apache.maven.enforcer.rule.api.EnforcerRuleHelper;
import org.apache.maven.plugin.logging.Log;
import org.codehaus.plexus.component.configurator.expression.ExpressionEvaluationException;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Verifies for usage of properties mentioned in .properties files.
 */
@SuppressWarnings("WeakerAccess")
public final class PropertyUsageRule implements EnforcerRule {

    /**
     * Properties which were defined more than once.
     */
    @Nonnull
    private final Map<String, Integer> propertiesDefinedMoreThanOnce = new HashMap<>();

    /**
     * Properties which were not found in usages.
     */
    @Nonnull
    private final Set<String> propertiesNotUsed = new HashSet<>();

    /**
     * Properties which were used in usages but not defined in definitions.
     */
    @Nonnull
    private final Set<UsageFiles.UsageLocation> propertiesNotDefined = new HashSet<>();

    /**
     * Logger given by Maven Enforcer.
     */
    Log log = null;

    //
    // Following variables match the configuration items and are populated by Maven/Enforcer,
    // despite being private.
    //

    /**
     * Activate definitionsOnlyOnce
     */
    private boolean definitionsOnlyOnce = true;

    /**
     * Activate definedPropertiesAreUsed
     */
    private boolean definedPropertiesAreUsed = true;

    /**
     * Activate usedPropertiesAreDefined
     */
    private boolean usedPropertiesAreDefined = true;

    /**
     * Replace this string with property name in template(s).
     */
    @Nonnull
    private String replaceInTemplateWithPropertyName = Templates.DEFAULT_REPLACE_IN_TEMPLATE_WITH_PROPERTY_NAME;

    /**
     * Definitions
     */
    @Nonnull
    private Collection<String> definitions = Definitions.getDefault();
    /**
     * Templates
     */
    @Nonnull
    private Collection<String> templates = Templates.getDefault();
    /**
     * Usages
     */
    @Nonnull
    private Collection<String> usages = Usages.getDefault();

    /**
     * @param helper EnforcerRuleHelper
     * @throws EnforcerRuleException Throws when error
     */
    @Override
    @SuppressWarnings({
            "squid:S1067", // Expressions should not be too complex
            "squid:S1192"  // String literals should not be duplicated
    })
    //@SuppressWarnings("squid:S1192")
    public void execute(@Nonnull final EnforcerRuleHelper helper)
            throws EnforcerRuleException {
        log = helper.getLog();

        Charset sourceEncoding = Charset.forName("UTF-8");
        try {
            sourceEncoding = Charset.forName(helper.evaluate("${project.build.sourceEncoding}").toString());
        } catch (ExpressionEvaluationException | NullPointerException e) {
            log.error("Cannot get property 'project.build.sourceEncoding'. Using default (UTF-8). Error:" + e);
        }

        log.debug("PropertyUsageRule:execute");
        log.debug(definitions.toString());
        log.debug(templates.toString());
        log.debug(usages.toString());

        try {
            // Get property definitions (i.e. property names):
            // Get all the files to read for the properties definitions.
            Files files = new Files(log);
            final Collection<String> propertyFilenames = files.getAbsoluteFilenames(definitions)
                    .stream().sorted().collect(Collectors.toSet());
            // Get the property definitions and how many times they are defined.
            Map<String, Integer> definedProperties;
            if (definitionsOnlyOnce) {
                definedProperties = new PropertyFiles(log).readPropertiesFromFilesWithCount(propertyFilenames);
                definedProperties.forEach((prop, nrOf) -> {
                    log.debug("Property '" + prop + "' defined " + nrOf + " times.");
                    if (nrOf > 1) {
                        propertiesDefinedMoreThanOnce.put(prop, nrOf);
                    }
                });
            } else {
                definedProperties = new PropertyFiles(log).readPropertiesFromFilesWithoutCount(propertyFilenames);
            }

            // Get all the files to check for property usage. *.java, *.jsp, etc.
            final Collection<String> usageFilenames = files.getAbsoluteFilenames(usages)
                    .stream().sorted().collect(Collectors.toSet());

            // Iterate through files and collect property usage.
            // Iterate
            UsageFiles usageFiles = new UsageFiles(log);
            if (definedPropertiesAreUsed) {
                final Map<String,String> readyTemplates = new HashMap<>();
                templates.forEach(tpl -> definedProperties.forEach(
                        (propertyDefinition, nrPropertyDefinitions) ->
                                readyTemplates.put(
                                        tpl.replaceAll(replaceInTemplateWithPropertyName, propertyDefinition),
                                        propertyDefinition
                                )
                        )
                );
                log.debug("readyTemplates:" + readyTemplates);
                final Collection<String> usedProperties
                        = usageFiles.readDefinedUsagesFromFiles(usageFilenames, readyTemplates, sourceEncoding);
                definedProperties.forEach((prop, nrOf) -> {
                    if (!usedProperties.contains(prop)) {
                        log.debug("Property " + prop + " not used.");
                        propertiesNotUsed.add(prop);
                    }
                });
            }
            if (usedPropertiesAreDefined) {
                final Set<String> readyTemplates = new HashSet<>();
                templates.forEach(tpl -> readyTemplates.add(
                                        tpl.replaceAll(replaceInTemplateWithPropertyName, Templates.PROPERTY_NAME_REGEXP)
                        )
                );
                log.debug("readyTemplates:" + readyTemplates);
                final Collection<UsageLocation> usageLocations
                        = usageFiles.readAllUsagesFromFiles(usageFilenames, readyTemplates, sourceEncoding);
                usageLocations.forEach(loc -> {
                    if (!definedProperties.containsKey(loc.getProperty())) {
                        log.debug("Property " + loc.getProperty() + " not defined.");
                        propertiesNotDefined.add(loc);
                    } else {
                        log.debug("Property " + loc.getProperty() + " defined.");
                    }
                });
            }
        } catch (IOException e) {
            throw new EnforcerRuleException(
                    "IO error: " + e.getLocalizedMessage(), e
            );
        }

        // Report errors in wanted categories:
        // propertiesDefinedMoreThanOnce
        if (definitionsOnlyOnce) {
            propertiesDefinedMoreThanOnce.forEach((key, value) ->
                    log.error("Property " + key + " defined " + value + " times!")
            );
        }
        // propertiesNotUsed
        if (definedPropertiesAreUsed) {
            propertiesNotUsed.forEach(key ->
                    log.error("Property " + key + " not used!")
            );
        }
        // propertiesNotDefined
        if (usedPropertiesAreDefined) {
            propertiesNotDefined.forEach(loc ->
                    log.error("Property " + loc.getProperty() + " used without defining it ("
                            + loc.getFilename() + ":" + loc.getRow() + ")"));
        }

        // Fail rule if errors in wanted categories.
        if ((definedPropertiesAreUsed && !propertiesNotUsed.isEmpty())
                || (usedPropertiesAreDefined && !propertiesNotDefined.isEmpty())
                || (definitionsOnlyOnce && !propertiesDefinedMoreThanOnce.isEmpty())
                ) {
            throw new EnforcerRuleException(
                    "Errors in property definitions or usage!"
            );
        }
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
     * Getters for results, used for testing.
     */

    @Nonnull
    public Set<String> getPropertiesNotUsed() {
        return propertiesNotUsed;
    }

    @Nonnull
    public Set<UsageFiles.UsageLocation> getPropertiesNotDefined() {
        return propertiesNotDefined;
    }

    @Nonnull
    public Map<String, Integer> getPropertiesDefinedMoreThanOnce() {
        return propertiesDefinedMoreThanOnce;
    }

    public boolean isDefinedPropertiesAreUsed() {
        return definedPropertiesAreUsed;
    }

    public void setDefinedPropertiesAreUsed(final boolean definedPropertiesAreUsed) {
        this.definedPropertiesAreUsed = definedPropertiesAreUsed;
    }

    public boolean isUsedPropertiesAreDefined() {
        return usedPropertiesAreDefined;
    }

    public void setUsedPropertiesAreDefined(final boolean usedPropertiesAreDefined) {
        this.usedPropertiesAreDefined = usedPropertiesAreDefined;
    }

    public boolean isDefinitionsOnlyOnce() {
        return definitionsOnlyOnce;
    }

    public void setDefinitionsOnlyOnce(final boolean definitionsOnlyOnce) {
        this.definitionsOnlyOnce = definitionsOnlyOnce;
    }

    @Nonnull
    public String getReplaceInTemplateWithPropertyName() {
        return replaceInTemplateWithPropertyName;
    }

    public void setReplaceInTemplateWithPropertyName(@Nonnull final String replaceInTemplateWithPropertyName) {
        this.replaceInTemplateWithPropertyName = replaceInTemplateWithPropertyName;
    }

    @Nonnull
    public Collection<String> getDefinitions() {
        return definitions;
    }

    /**
     * Setters for the parameters
     * (these are not used by Maven Enforcer, used for testing).
     */

    public void setDefinitions(@Nonnull final Collection<String> definitions) {
        this.definitions = definitions;
    }

    @Nonnull
    public Collection<String> getTemplates() {
        return templates;
    }

    public void setTemplates(@Nonnull final Collection<String> templates) {
        this.templates = templates;
    }

    @Nonnull
    public Collection<String> getUsages() {
        return usages;
    }

    public void setUsages(@Nonnull Collection<String> usages) {
        this.usages = usages;
    }
}
