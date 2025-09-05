package com.weatherapi.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Config {
    private static final Logger logger = LoggerFactory.getLogger(Config.class);
    private static Properties properties;
    private static final String DEFAULT_CONFIG_FILE = "config/test.properties";

    static {
        loadProperties();
    }

    private static void loadProperties() {
        properties = new Properties();
        String configFile = System.getProperty("config.file", DEFAULT_CONFIG_FILE);

        try (InputStream inputStream = Config.class.getClassLoader()
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
        String apiKey = System.getenv("OPENWEATHER_API_KEY");
        if (apiKey != null && !apiKey.isEmpty()) {
            System.out.println("Using environment API key: " + apiKey.substring(0, 8) + "...");
            return apiKey;
        }

        String propKey = properties.getProperty("api.key");
        System.out.println("Using properties API key: " + (propKey != null ? propKey.substring(0, 8) + "..." : "null"));
        return propKey;
    }

    public static int getApiTimeout() {
        return Integer.parseInt(properties.getProperty("api.timeout", "5000"));
    }

    public static String getDefaultCity() {
        return properties.getProperty("default.city", "Foley");
    }

    public static String getDefaultCountry() {
        return properties.getProperty("default.country", "US");
    }

    public static String getInvalidCity() {
        return properties.getProperty("default.invalid.city", "NonExistentCity123");
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