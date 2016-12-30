package com.github.mikkoi.maven.plugins.enforcer.rule.propertyusage;

import com.github.mikkoi.maven.plugins.enforcer.rule.propertyusage.configuration.Files;
import org.apache.maven.enforcer.rule.api.EnforcerRuleException;
import org.apache.maven.enforcer.rule.api.EnforcerRuleHelper;
import org.apache.maven.plugins.enforcer.EnforcerTestUtils;
import org.junit.Test;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Test class PropertyUsageRule.
 */
public final class TestPropertyUsageRule {

    //    @Test
//    public void testValidFiles1() {
//        testFiles(true, 0, "UTF-8", "src/test/resources", "utf-8.txt", StringUtils.EMPTY);
//    }
//
//    @Test
//    public void testValidFiles1_1() {
//        testFiles(true, 0, "ISO-8859-1", "src/test/resources", "iso.txt", StringUtils.EMPTY);
//    }
//
//    @Test
//    public void testValidFiles1_2() {
//        testFiles(false, 1, "UTF-8", "src/test/resources", StringUtils.EMPTY, StringUtils.EMPTY);
//    }
//
//    @Test
//    public void testValidFiles2() {
//        testFiles(false, 2, "ASCII", "src/test/resources", StringUtils.EMPTY, StringUtils.EMPTY);
//    }
//
//    @Test
//    public void testValidFiles3() {
//        // TestClassInUTF8 can be interpreted as iso-8859-1
//        testFiles(true, 0, "iso-8859-1", "src/test/java", StringUtils.EMPTY, StringUtils.EMPTY);
//    }
//
//    @Test
//    public void testValidFiles4() {
//        // This file (officially - defined by Maven - as utf-8) has no 8-bit chars so it can be ANY encoding!
//        testFiles(true, 0, "utf-8", "src/test/java", StringUtils.EMPTY, StringUtils.EMPTY);
//    }
//
//    @Test
//    public void testValidFiles5() {
//        testFiles(false, 1, "ascii", "src/test/java", StringUtils.EMPTY, StringUtils.EMPTY);
//    }
//
//    @Test
//    public void testValidFiles6() {
//        testFiles(true, 0, "ISO-8859-1", "src/test/resources", StringUtils.EMPTY, StringUtils.EMPTY);
//    }
//
//    private void testFiles(
//            boolean expected,
//            int expectedFaultyFilesAmount,
//            @Nonnull String encoding,
//            @Nonnull String dir,
//            @Nonnull String includeRegex,
//            @Nonnull String excludeRegex
//    ) {
//        boolean isValid;
//        PropertyUsageRule rule = new PropertyUsageRule();
//        rule.setRequireEncoding(encoding);
//        rule.setDirectory(dir);
//        rule.setIncludeRegex(includeRegex);
//        rule.setExcludeRegex(excludeRegex);
//        try {
//            EnforcerRuleHelper helper = EnforcerTestUtils.getHelper();
//            rule.execute(helper);
//            isValid = true;
//        } catch (EnforcerRuleException e) {
//            // e.printStackTrace();
//            isValid = false;
//        }
//        assertTrue(isValid == expected && rule.getFaultyFiles().size() == expectedFaultyFilesAmount);
//    }
//
//    @Test
//    public void testApp1AllDefaults() {
//        testProps(true, Collections.emptySet(), Collections.emptySet(),
//                null, null, null);
//    }
//
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
            boolean expected, // Rule passes.
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
            if (!expected) {
                System.out.println(e.getLongMessage());
            }
            isValid = false;
        }
        assertTrue(isValid == expected);
        assertTrue(rule.getPropertiesNotUsed().equals(propertiesNotUsed));
        final Set<String> resultPropertiesNotDefined = new HashSet<>();
        rule.getPropertiesNotDefined().forEach(val -> resultPropertiesNotDefined.add(val.getProperty()));
        assertTrue(resultPropertiesNotDefined.equals(propertiesNotDefined));
        assertTrue(rule.getPropertiesDefinedMoreThanOnce().keySet().equals(propertiesDefinedMoreThanOnce));
    }

    // Implement feature first.
//    @Test
//    public void testPropertyUsageRuleDefinitionsOnlyOnceFail() {
//        final Collection<String> propertiesDefinedMoreThanOnce = new HashSet<>();
//        propertiesDefinedMoreThanOnce.add("my.property.value");
//        propertiesDefinedMoreThanOnce.add("third.property.value");
//        testProps(
//                true,
//                false,
//                false,
//                null,
//                Collections.singleton(Files.absoluteCwdAndFile("src/test/resources/app1-double-def.properties")),
//                null,
//                null,
//                false,
//                propertiesDefinedMoreThanOnce,
//                Collections.emptySet(),
//                Collections.emptySet()
//        );
//    }
//
//    @Test
//    public void testPropertyUsageRuleDefinitionsOnlyOnceOk() {
//        testProps(
//                true,
//                false,
//                false,
//                null,
//                Collections.singleton(Files.absoluteCwdAndFile("src/test/resources/app1-double-def.properties")),
//                null,
//                null,
//                true,
//                Collections.emptySet(),
//                Collections.emptySet(),
//                Collections.emptySet()
//        );
//    }
//
    @Test
    public void testPropertyUsageRuleDefinedOk() {
        testProps(
                false,
                true,
                false,
                null,
                Collections.singleton(Files.absoluteCwdAndFile("src/test/resources/app1.properties")),
                Collections.singleton("properties.getProperty(\"REPLACE_THIS\")"),
                Collections.singleton(Files.absoluteCwdAndFile("src/test/java/com/github/mikkoi/maven/plugins/enforcer/rule/propertyusage/App1.java")),
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
                Collections.singleton(Files.absoluteCwdAndFile("src/test/resources/app1.properties")),
                null,
                Collections.singleton(Files.absoluteCwdAndFile("src/test/java/com/github/mikkoi/maven/plugins/enforcer/rule/propertyusage/App1.java")),
                true,
                Collections.emptySet(),
                Collections.emptySet(),
                Collections.emptySet()
        );
    }

    @Test
    public void testPropertyUsageRuleDefinedOk3() {
        final Collection<String> templates = new HashSet<>();
        templates.add("properties.getProperty(\"REPLACE_THIS\")");
        templates.add("${REPLACE_THIS}");
        testProps(
                false,
                true,
                false,
                null,
                Collections.singleton(Files.absoluteCwdAndFile("src/test/resources/app1.properties")),
                templates,
                Collections.singleton(Files.absoluteCwdAndFile("src/test/java/com/github/mikkoi/maven/plugins/enforcer/rule/propertyusage/App1.java")),
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
        testProps(
                false,
                true,
                false,
                null,
                Collections.singleton(Files.absoluteCwdAndFile("src/test/resources/app2.properties")),
                null,
                Collections.singleton(Files.absoluteCwdAndFile("src/test/java/com/github/mikkoi/maven/plugins/enforcer/rule/propertyusage/App1.java")),
                false,
                Collections.emptySet(),
                propertiesNotUsed,
                Collections.emptySet()
        );
    }

    @Test
    public void testPropertyUsageRuleDefinedFail2() {
        final Collection<String> templates = new HashSet<>();
        templates.add("properties.getProperty(\"REPLACE_THIS\")");
        templates.add("${REPLACE_THIS}");
        final Collection<String> propertiesNotUsed = new HashSet<>();
        propertiesNotUsed.add("my-too.property.value");
        propertiesNotUsed.add("other-too.prop.val");
        testProps(
                false,
                true,
                false,
                null,
                Collections.singleton(Files.absoluteCwdAndFile("src/test/resources/app2.properties")),
                templates,
                Collections.singleton(Files.absoluteCwdAndFile("src/test/java/com/github/mikkoi/maven/plugins/enforcer/rule/propertyusage/App1.java")),
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
                Collections.singleton(Files.absoluteCwdAndFile("src/test/resources/app1.properties")),
                Collections.singleton("properties.getProperty(\"REPLACE_THIS\")"),
                Collections.singleton(Files.absoluteCwdAndFile("src/test/java/com/github/mikkoi/maven/plugins/enforcer/rule/propertyusage/App1.java")),
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
                Collections.singleton(Files.absoluteCwdAndFile("src/test/resources/app2.properties")),
                null,
                Collections.singleton(Files.absoluteCwdAndFile("src/test/java/com/github/mikkoi/maven/plugins/enforcer/rule/propertyusage/App2.java")),
                true,
                Collections.emptySet(),
                Collections.emptySet(),
                Collections.emptySet()
        );
    }

    @Test
    public void testPropertyUsageRuleUsedFail2() {
        testProps(
                false,
                false,
                true,
                null,
                Collections.singleton(Files.absoluteCwdAndFile("src/test/resources/app2.properties")),
                null,
                Collections.singleton(Files.absoluteCwdAndFile("src/test/java/com/github/mikkoi/maven/plugins/enforcer/rule/propertyusage/App2.java")),
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
                Collections.singleton(Files.absoluteCwdAndFile("src/test/resources/empty.properties")),
                templates,
                Collections.singleton(Files.absoluteCwdAndFile("src/test/java/com/github/mikkoi/maven/plugins/enforcer/rule/propertyusage/App3.java")),
                false,
                Collections.emptySet(),
                Collections.emptySet(),
                propertiesNotDefined
        );
    }

//    @Test
//    public void testReadPropertiesWithDefaultConfiguration() {
//        Map<String, Integer> expected = Maps.newHashMap();
//        expected.put("my.property.value", 0);
//        expected.put("other.prop.val", 0);
//        Collection<Collection<FileSpec>> definitions = Collections.singletonList(Collections.singletonList(new FileSpec("src/test/resources/app1.properties")));
//        Map<String, Integer> properties = PropertyUsageRule.readProperties(definitions);
//        assertEquals("Read properties are as expected", expected, properties);
//
//    }

    @Test
    public void testDefaultValues() {
        PropertyUsageRule rule = new PropertyUsageRule();
        assertEquals("Default definitionsOnlyOnce is true", true, rule.isDefinitionsOnlyOnce());
        assertEquals("Default definedPropertiesAreUsed is true", true, rule.isDefinedPropertiesAreUsed());
        assertEquals("Default usedPropertiesAreDefined is true", true, rule.isUsedPropertiesAreDefined());
        assertEquals("Default replaceInTemplateWithPropertyName is correct", "REPLACE_THIS", rule.getReplaceInTemplateWithPropertyName());
        assertEquals("Default definitions are correct", Collections.singleton(Files.absoluteCwdAndFile("src/main/resources")), rule.getDefinitions());
        assertEquals("Default templates are correct", Collections.singleton("\"REPLACE_THIS\""), rule.getTemplates());
        assertEquals("Default usages are correct", Collections.singleton(Files.absoluteCwdAndFile("src/main/java")), rule.getUsages());
    }
}
