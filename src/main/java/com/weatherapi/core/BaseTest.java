package com.weatherapi.core;

import com.weatherapi.config.TestConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;

import java.lang.reflect.Method;

public class BaseTest {
    protected static final Logger logger = LoggerFactory.getLogger(BaseTest.class);
    protected WeatherApiClient weatherApiClient;
    protected static final String city = TestConfig.getDefaultCity();
    protected static final String countryCode = TestConfig.getDefaultCountry();
    protected static final String invalidCity = TestConfig.getInvalidCity();
    protected static final String cityWithSpecialCharacters = "São Paulo";

    @BeforeClass
    public void setupClass() {
        logger.info("Setting up test class: {}", this.getClass().getSimpleName());

        // Initialize API client with configuration
        weatherApiClient = new WeatherApiClient(TestConfig.getAllProperties());

        // Validate API key is working
        if (!weatherApiClient.validateApiKey()) {
            throw new RuntimeException("API key validation failed. Please check your configuration.");
        }

        logger.info("Test setup completed successfully");
    }

    @BeforeMethod
    public void setupMethod(Method method) {
        logger.info("Starting test method: {}", method.getName());
    }

    /**
     * Common assertion helper for API response validation
     */
    protected void validateSuccessfulResponse(io.restassured.response.Response response) {
        response.then()
                .statusCode(200)
                .header("Content-Type", org.hamcrest.Matchers.containsString("application/json"));
    }

    /**
     * Helper method to log test completion
     */
    protected void logTestCompletion(String testName, boolean passed) {
        logger.info("Test '{}' completed. Status: {}", testName, passed ? "PASSED" : "FAILED");
    }
}