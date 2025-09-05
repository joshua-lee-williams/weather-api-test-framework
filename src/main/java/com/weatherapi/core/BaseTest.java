package com.weatherapi.core;

import com.weatherapi.config.TestConfig;
import io.restassured.response.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;

import java.lang.reflect.Method;

import static org.hamcrest.Matchers.containsString;

public class BaseTest {
    protected static final Logger logger = LoggerFactory.getLogger(BaseTest.class);
    protected WeatherApiClient weatherApiClient;
    protected static final String DEFAULT_CITY = TestConfig.getDefaultCity();
    protected static final String DEFAULT_COUNTRY_CODE = TestConfig.getDefaultCountry();
    protected static final String INVALID_CITY = TestConfig.getInvalidCity();
    protected static final String CITY_WITH_SPECIAL_CHARACTERS = "São Paulo";
    protected static final int CITY_NOT_FOUND_STATUS = 404;
    protected static final String CITY_NOT_FOUND_MESSAGE_CONTAINS = "not found";

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


    protected void validateSuccessfulResponse(io.restassured.response.Response response) {
        response.then()
                .statusCode(200)
                .header("Content-Type", containsString("application/json"));
    }

    protected void logTestCompletion(String testName, boolean passed) {
        logger.info("Test '{}' completed. Status: {}", testName, passed ? "PASSED" : "FAILED");
    }

    protected void validateErrorResponse(Response response, int expectedStatus, String expectedMessageContains) {
        response.then()
                .statusCode(expectedStatus)
                .body("message", containsString(expectedMessageContains));
    }
}