package com.github.mikkoi.maven.plugins.enforcer.rule.propertyusage;

import com.github.mikkoi.maven.plugins.enforcer.rule.propertyusage.configuration.FileSpecs;
import org.apache.maven.enforcer.rule.api.EnforcerRuleException;
import org.apache.maven.enforcer.rule.api.EnforcerRuleHelper;
import org.apache.maven.plugins.enforcer.EnforcerTestUtils;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.File;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Test class PropertyUsageRule.
 */
public final class TestPropertyUsageRule {

    private void testProps(
            // Configuration (null value => use defaults)
            @Nullable final Boolean definitionsOnlyOnce,
            @Nullable final Boolean definedPropertiesAreUsed,
            @Nullable final Boolean usedPropertiesAreDefined,
            @Nullable final String replaceInTemplateWithPropertyName,
            @Nullable final Collection<String> definitions,
            @Nullable final Collection<String> templates,
            @Nullable final Collection<String> usages,
            // Results
            boolean rulePasses, // Rule passes.
            @Nonnull final Collection<String> propertiesDefinedMoreThanOnce, // There names were defined more than once.
            @Nonnull final Collection<String> propertiesNotUsed, // These names were never used.
            @Nonnull final Collection<String> propertiesNotDefined // These names were not defined.
    ) {
        boolean isValid;
        PropertyUsageRule rule = new PropertyUsageRule();

        // Configuration.
        if (definitionsOnlyOnce != null) {
            rule.setDefinitionsOnlyOnce(definitionsOnlyOnce);
        }
        if (definedPropertiesAreUsed != null) {
            rule.setDefinedPropertiesAreUsed(definedPropertiesAreUsed);
        }
        if (usedPropertiesAreDefined != null) {
            rule.setUsedPropertiesAreDefined(usedPropertiesAreDefined);
        }
        if (definitions != null) {
            rule.setDefinitions(definitions);
        }
        if (templates != null) {
            rule.setTemplates(templates);
        }
        if (usages != null) {
            rule.setUsages(usages);
        }
        if (replaceInTemplateWithPropertyName != null) {
            rule.setReplaceInTemplateWithPropertyName(replaceInTemplateWithPropertyName);
        }

        // Run rule.
        try {
            EnforcerRuleHelper helper = EnforcerTestUtils.getHelper();
            rule.execute(helper);
            isValid = true;
        } catch (EnforcerRuleException e) {
            if (!rulePasses) {
                System.err.println("Rule broken. Error:" + e.getLocalizedMessage());
            }
            isValid = false;
        }
        assertTrue("Success or failure is as expected.", isValid == rulePasses);
        assertTrue("Properties not used as expected.", !rule.isDefinedPropertiesAreUsed() || rule.getPropertiesNotUsed().equals(propertiesNotUsed));
        final Set<String> resultPropertiesNodDefined = new HashSet<>();
        rule.getPropertiesNotDefined().forEach(val -> resultPropertiesNodDefined.add(val.getProperty()));
        assertTrue("Properties not defined as expected.", rule.isUsedPropertiesAreDefined() || resultPropertiesNodDefined.equals(propertiesNotDefined));
        assertTrue("Properties defined more than once as expected.", rule.getPropertiesDefinedMoreThanOnce().keySet().equals(propertiesDefinedMoreThanOnce));
    }

    // Implement feature first.
    @Test
    public void testPropertyUsageRuleDefinitionsOnlyOnceFail() {
        final Collection<String> propertiesDefinedMoreThanOnce = new HashSet<>();
        propertiesDefinedMoreThanOnce.add("first.property.value");
        propertiesDefinedMoreThanOnce.add("third.property.value");
        testProps(
                true,
                false,
                false,
                null,
                Collections.singleton(FileSpecs.absoluteCwdAndFile("src/test/resources/app1-double-def.properties")),
                null,
                null,
                false,
                propertiesDefinedMoreThanOnce,
                Collections.emptySet(),
                Collections.emptySet()
        );
    }

    @Test
    public void testPropertyUsageRuleDefinitionsOnlyOnceOk() {
        testProps(
                true,
                false,
                false,
                null,
                Collections.singleton(FileSpecs.absoluteCwdAndFile("src/test/resources/app1.properties")),
                null,
                null,
                true,
                Collections.emptySet(),
                Collections.emptySet(),
                Collections.emptySet()
        );
    }

    @Test
    public void testPropertyUsageRuleDefinedOk() {
        testProps(
                false,
                true,
                false,
                null,
                Collections.singleton(FileSpecs.absoluteCwdAndFile("src/test/resources/app1.properties")),
                Collections.singleton("properties\\.getProperty\\(\"REPLACE_THIS\"\\)"),
                Collections.singleton(FileSpecs.absoluteCwdAndFile("src/test/java/com/github/mikkoi/maven/plugins/enforcer/rule/propertyusage/App1.java")),
                true,
                Collections.emptySet(),
                Collections.emptySet(),
                Collections.emptySet()
        );
    }

    @Test
    public void testPropertyUsageRuleDefinedOk2() {
        testProps(
                false,
                true,
                false,
                null,
                Collections.singleton(FileSpecs.absoluteCwdAndFile("src/test/resources/app1.properties")),
                null,
                Collections.singleton(FileSpecs.absoluteCwdAndFile("src/test/java/com/github/mikkoi/maven/plugins/enforcer/rule/propertyusage/App1.java")),
                true,
                Collections.emptySet(),
                Collections.emptySet(),
                Collections.emptySet()
        );
    }

    @Test
    public void testPropertyUsageRuleDefinedOk3() {
        final Collection<String> templates = new HashSet<>();
        templates.add("properties\\.getProperty\\(\"REPLACE_THIS\"\\)");
        templates.add("\\$\\{REPLACE_THIS\\}");
        testProps(
                false,
                true,
                false,
                null,
                Collections.singleton(FileSpecs.absoluteCwdAndFile("src/test/resources/app1.properties")),
                templates,
                Collections.singleton(FileSpecs.absoluteCwdAndFile("src/test/java/com/github/mikkoi/maven/plugins/enforcer/rule/propertyusage/App1.java")),
                true,
                Collections.emptySet(),
                Collections.emptySet(),
                Collections.emptySet()
        );
    }

    @Test
    public void testPropertyUsageRuleDefinedFail1() {
        final Collection<String> propertiesNotUsed = new HashSet<>();
        propertiesNotUsed.add("my-too.property.value");
        propertiesNotUsed.add("other-too.prop.val");
        propertiesNotUsed.add("also-prop.val");
        testProps(
                false,
                true,
                false,
                null,
                Collections.singleton(FileSpecs.absoluteCwdAndFile("src/test/resources/app2.properties")),
                null,
                Collections.singleton(FileSpecs.absoluteCwdAndFile("src/test/java/com/github/mikkoi/maven/plugins/enforcer/rule/propertyusage/App1.java")),
                false,
                Collections.emptySet(),
                propertiesNotUsed,
                Collections.emptySet()
        );
    }

    @Test
    public void testPropertyUsageRuleDefinedFail2() {
        final Collection<String> templates = new HashSet<>();
        templates.add("properties\\.getProperty\\(\"REPLACE_THIS\"\\)");
        templates.add("\\$\\{REPLACE_THIS\\}");
        final Collection<String> propertiesNotUsed = new HashSet<>();
        propertiesNotUsed.add("my-too.property.value");
        propertiesNotUsed.add("other-too.prop.val");
        propertiesNotUsed.add("also-prop.val");
        testProps(
                false,
                true,
                false,
                null,
                Collections.singleton(FileSpecs.absoluteCwdAndFile("src/test/resources/app2.properties")),
                templates,
                Collections.singleton(FileSpecs.absoluteCwdAndFile("src/test/java/com/github/mikkoi/maven/plugins/enforcer/rule/propertyusage/App1.java")),
                false,
                Collections.emptySet(),
                propertiesNotUsed,
                Collections.emptySet()
        );
    }

    @Test
    public void testPropertyUsageRuleUsedOk1() {
        testProps(
                false,
                false,
                true,
                null,
                Collections.singleton(FileSpecs.absoluteCwdAndFile("src/test/resources/app1.properties")),
                Collections.singleton("properties.getProperty(\"REPLACE_THIS\")"),
                Collections.singleton(FileSpecs.absoluteCwdAndFile("src/test/java/com/github/mikkoi/maven/plugins/enforcer/rule/propertyusage/App1.java")),
                true,
                Collections.emptySet(),
                Collections.emptySet(),
                Collections.emptySet()
        );
    }

    @Test
    public void testPropertyUsageRuleUsedOk2() {
        testProps(
                false,
                false,
                true,
                null,
                Collections.singleton(FileSpecs.absoluteCwdAndFile("src/test/resources/app2.properties")),
                null,
                Collections.singleton(FileSpecs.absoluteCwdAndFile("src/test/java/com/github/mikkoi/maven/plugins/enforcer/rule/propertyusage/App2.java")),
                true,
                Collections.emptySet(),
                Collections.emptySet(),
                Collections.emptySet()
        );
    }

    @Test
    public void testPropertyUsageRuleUsedOk3() {
        final Collection<String> templates = new HashSet<>();
        templates.add("properties\\.getProperty\\(\"REPLACE_THIS\"\\)");
        templates.add("\\$\\{REPLACE_THIS\\}");
        testProps(
                false,
                false,
                true,
                null,
                Collections.singleton(FileSpecs.absoluteCwdAndFile("src/test/resources/app2.properties")),
                templates,
                Collections.singleton(FileSpecs.absoluteCwdAndFile("src/test/java/com/github/mikkoi/maven/plugins/enforcer/rule/propertyusage/App2.java")),
                true,
                Collections.emptySet(),
                Collections.emptySet(),
                Collections.emptySet()
        );
    }

    @Test
    public void testPropertyUsageRuleUsedFail3() {
        final Collection<String> templates = new HashSet<>();
        templates.add("properties.getProperty\\(\"REPLACE_THIS\"\\)");
        templates.add("\\$\\{REPLACE_THIS\\}");
        Collection<String> propertiesNotDefined = new HashSet<>();
        propertiesNotDefined.add("my-second.prop.val");
        propertiesNotDefined.add("my-first.property.value");
        propertiesNotDefined.add("my-third-val");
        testProps(
                false,
                false,
                true,
                null,
                Collections.singleton(FileSpecs.absoluteCwdAndFile("src/test/resources/empty.properties")),
                templates,
                Collections.singleton(FileSpecs.absoluteCwdAndFile("src/test/java/com/github/mikkoi/maven/plugins/enforcer/rule/propertyusage/App3.java")),
                false,
                Collections.emptySet(),
                Collections.emptySet(),
                propertiesNotDefined
        );
    }

    @Test
    public void testPropertyUsageRuleDefinedFail_Wildcard1() {
        final Collection<String> templates = new HashSet<>();
        templates.add("properties\\.getProperty\\(\"REPLACE_ME_HERE\"\\)");
        Collection<String> propertiesNotUsed = new HashSet<>();
        propertiesNotUsed.add("my-second.prop.val");
        propertiesNotUsed.add("my-first.property.value");
        propertiesNotUsed.add("my-third-val");
        testProps(
                false,
                false,
                true,
                null,
                Collections.singleton("src/test/resources/*.properties"),
                templates,
                Collections.singleton("src/test/java/**/AppEmpty.java"),
                true, // No properties found, so all "found" properties are defined!
                Collections.emptySet(),
                propertiesNotUsed,
                Collections.emptySet()
        );
    }

    @Test
    public void testPropertyUsageRuleDefinedFail_Wildcard2() throws Exception {
        File tempFile = testDir.newFile();
        assertTrue(tempFile.canWrite());
        assertTrue(tempFile.setWritable(true));
        Files.write(tempFile.getAbsoluteFile().toPath(), "very-temporary-property.value=Meaningless value".getBytes(Charset.forName("UTF-8")), StandardOpenOption.WRITE);
        final Collection<String> templates = new HashSet<>();
        templates.add("properties\\.getProperty\\(\"REPLACE_ME_HERE\"\\)");
        templates.add("\\$\\{REPLACE_ME_HERE\\}");
        Collection<String> definitions = new HashSet<>();
        definitions.add("src/test/resources/**/*.properties");
        definitions.add("");
        definitions.add("src/test/resources/app1.properties");
        definitions.add(tempFile.getAbsolutePath());
        definitions.add("src/test/resources/properties-dir/sub-app-props2.properties");
        Collection<String> propertiesNotUsed = new HashSet<>();
        propertiesNotUsed.add("subapp.other.prop.val");
        propertiesNotUsed.add("third.property.value");
        propertiesNotUsed.add("subapp.my2.property.value");
        propertiesNotUsed.add("subapp.my.property.value");
        propertiesNotUsed.add("subapp.other2.prop.val");
        propertiesNotUsed.add("very-temporary-property.value");
        propertiesNotUsed.add("first.property.value");
        propertiesNotUsed.add("second.property.value");
        propertiesNotUsed.add("fourth.property.value");

        testProps(
                false,
                true,
                true,
                "REPLACE_ME_HERE",
                definitions,
                templates,
                Collections.singleton("src/test/java/**/App*.java"),
                false,
                Collections.emptySet(),
                propertiesNotUsed,
                Collections.emptySet()
        );
    }

    @Test
    public void testDefaultValues() {
        PropertyUsageRule rule = new PropertyUsageRule();
        assertEquals("Default definitionsOnlyOnce is true.", true, rule.isDefinitionsOnlyOnce());
        assertEquals("Default definedPropertiesAreUsed is true.", true, rule.isDefinedPropertiesAreUsed());
        assertEquals("Default usedPropertiesAreDefined is true.", true, rule.isUsedPropertiesAreDefined());
        assertEquals("Default replaceInTemplateWithPropertyName is correct.", "REPLACE_THIS", rule.getReplaceInTemplateWithPropertyName());
        assertEquals("Default definitions are correct.", Collections.singleton("src/main/resources/**/*.properties"), rule.getDefinitions());
        assertEquals("Default templates are correct.", Collections.singleton("\"REPLACE_THIS\""), rule.getTemplates());
        assertEquals("Default usages are correct.", Collections.singleton("src/main/java/**/*.java"), rule.getUsages());
    }

    @Rule
    public TemporaryFolder testDir = new TemporaryFolder();

}
