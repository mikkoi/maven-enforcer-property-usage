package com.github.mikkoi.maven.plugins.enforcer.rule.propertyusage;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

public class App1 {

    public String myPropertyValue = "";

    public Integer otherPropVal = 0;

    public App1() {
        Properties properties = new Properties();
        try (InputStream inputStream = new FileInputStream("app1.properties") ) {
            properties.load(inputStream);
            myPropertyValue = properties.getProperty("my.property.value");
            otherPropVal = Integer.valueOf(properties.getProperty("other.prop.value"));

        } catch (Exception e) {
            System.out.print(e.getMessage());
        }

    }
}
