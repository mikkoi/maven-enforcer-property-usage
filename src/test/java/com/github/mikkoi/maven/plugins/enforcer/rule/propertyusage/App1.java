package com.github.mikkoi.maven.plugins.enforcer.rule.propertyusage;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

@SuppressWarnings({"all"})
public class App1 {

    private String myPropertyValue = "";

    private Integer otherPropVal = 0;

    private App1() {
        Properties properties = new Properties();
        try (InputStream inputStream = new FileInputStream("app1.properties") ) {
            properties.load(inputStream);
            myPropertyValue = properties.getProperty("my.property.value");
            otherPropVal = Integer.valueOf(properties.getProperty("other.prop.val"));

        } catch (IOException e) {
            System.out.print(e.getMessage());
        }

    }
}
