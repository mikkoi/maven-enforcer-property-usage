package com.github.mikkoi.maven.plugins.enforcer.rule.propertyusage;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

public class App2 {

    public String value1 = "";

    public Integer value2 = 0x0;

    public double value3 = Double.valueOf("${also-prop.val}");

    public App2() {
        Properties properties = new Properties();
        try (InputStream inputStream = new FileInputStream("app2.properties") ) {
            properties.load(inputStream);
            value1 = properties.getProperty("my-too.property.value");
            value2 = Integer.valueOf(properties.getProperty("other-too.prop.val"));

        } catch (Exception e) {
            System.out.print(e.getMessage());
        }

    }
}
