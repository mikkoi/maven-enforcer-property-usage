package com.github.mikkoi.maven.plugins.enforcer.rule.propertyusage;

import com.github.mikkoi.maven.plugins.enforcer.rule.propertyusage.configuration.Template;
import org.apache.maven.enforcer.rule.api.EnforcerRuleException;
import org.apache.maven.enforcer.rule.api.EnforcerRuleHelper;
import org.apache.maven.plugins.enforcer.EnforcerTestUtils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Set;

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
            boolean expected,
            @Nonnull final Set<String> propertiesNotUsed, // There names were never used.
            @Nonnull final Set<String> propertiesNotDefined, // There names were not defined.
            @Nullable final Collection<Collection<String>> definitions,
            @Nullable final Collection<Template> templates,
            @Nullable final Collection<Collection<String>> usages
    ) {
        boolean isValid;
        PropertyUsageRule rule = new PropertyUsageRule();
        if (definitions != null) {
            rule.setDefinitions(definitions);
        }
        if (templates != null) {
            rule.setTemplates(templates);
        }
        if (usages != null) {
            rule.setUsages(usages);
        }
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
        assertTrue(rule.getPropertiesNotDefined().equals(propertiesNotDefined));
    }

//    @Test
//    public void testReadPropertiesDefault() {
//        Map<String, Integer> expected = Maps.newHashMap();
//        expected.put("my.property.value", 0);
//        expected.put("other.prop.val", 0);
//        Collection<Collection<FileSpec>> definitions = Collections.singletonList(Collections.singletonList(new FileSpec("src/test/resources/app1.properties")));
//        Map<String, Integer> properties = PropertyUsageRule.readProperties(definitions);
//        assertEquals("Read properties are as expected", expected, properties);
//
//    }

}
