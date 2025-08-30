package com.weatherapi.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class TestConfig {
    private static final Logger logger = LoggerFactory.getLogger(TestConfig.class);
    private static Properties properties;
    private static final String DEFAULT_CONFIG_FILE = "config/test.properties";

    static {
        loadProperties();
    }

    private static void loadProperties() {
        properties = new Properties();
        String configFile = System.getProperty("config.file", DEFAULT_CONFIG_FILE);

        try (InputStream inputStream = TestConfig.class.getClassLoader()
                .getResourceAsStream(configFile)) {

            if (inputStream != null) {
                properties.load(inputStream);
                logger.info("Configuration loaded from: {}", configFile);
            } else {
                logger.error("Configuration file not found: {}", configFile);
                throw new RuntimeException("Configuration file not found: " + configFile);
            }
        } catch (IOException e) {
            logger.error("Error loading configuration file: {}", configFile, e);
            throw new RuntimeException("Failed to load configuration", e);
        }
    }

    public static String getApiBaseUrl() {
        return properties.getProperty("api.base.url");
    }

    public static String getApiKey() {
        String apiKey = System.getProperty("api.key");
        if (apiKey != null && !apiKey.isEmpty()) {
            return apiKey;
        }
        return properties.getProperty("api.key");
    }

    public static int getApiTimeout() {
        return Integer.parseInt(properties.getProperty("api.timeout", "5000"));
    }

    public static String getDefaultCity() {
        return properties.getProperty("default.city", "London");
    }

    public static String getDefaultCountry() {
        return properties.getProperty("default.country", "UK");
    }

    public static Properties getAllProperties() {
        return new Properties(properties);
    }

    /**
     * Get property with fallback to system property
     */
    public static String getProperty(String key, String defaultValue) {
        String systemValue = System.getProperty(key);
        if (systemValue != null && !systemValue.isEmpty()) {
            return systemValue;
        }
        return properties.getProperty(key, defaultValue);
    }
}