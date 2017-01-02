package com.github.mikkoi.maven.plugins.enforcer.rule.propertyusage;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

@SuppressWarnings("all")
public class AppEmpty {

    public AppEmpty() {
        Properties properties = new Properties();
        try (InputStream inputStream = new FileInputStream("empty.properties") ) {
            properties.load(inputStream);
        } catch (IOException e) {
            System.out.print(e.getMessage());
        }

    }
}
