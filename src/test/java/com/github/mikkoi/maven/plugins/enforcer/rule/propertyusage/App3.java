package com.github.mikkoi.maven.plugins.enforcer.rule.propertyusage;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

@SuppressWarnings("all")
public class App3 {

    public String value1 = "";

    public Integer value2 = 0x0;

    public double value3 = Double.valueOf("${my-third-val}");

    public App3() {
        Properties properties = new Properties();
        try (InputStream inputStream = new FileInputStream("app3.properties") ) {
            properties.load(inputStream);
            value1 = properties.getProperty("my-first.property.value");
            value2 = Integer.valueOf(properties.getProperty("my-second.prop.val"));

        } catch (Exception e) {
            System.out.print(e.getMessage());
        }

    }
}
