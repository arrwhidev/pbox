package com.arrwhi.pbox.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Created by arran on 16/07/16.
 */
public class PropertiesHelper {

    private final static String PROPERTIES_FILENAME = "application.properties";
    private static Properties PROPERTIES;

    private PropertiesHelper() {}

    public static String get(String prop) {
        if(PROPERTIES == null) {
            loadProperties();
        }

        return PROPERTIES.getProperty(prop);
    }

    private static void loadProperties() {
        try (InputStream in =  PropertiesHelper.class.getClassLoader().getResourceAsStream(PROPERTIES_FILENAME)) {
            PROPERTIES = new Properties();
            PROPERTIES.load(in);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
