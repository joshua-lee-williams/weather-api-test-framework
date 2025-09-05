package com.weatherapi.core;

import com.weatherapi.config.Config;
import io.restassured.response.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;

import java.lang.reflect.Method;

import static org.hamcrest.Matchers.*;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.not;

public class BaseTest {
    protected static final Logger logger = LoggerFactory.getLogger(BaseTest.class);
    protected WeatherApiClient weatherApiClient;
    protected static final String DEFAULT_CITY = Config.getDefaultCity();
    protected static final String DEFAULT_COUNTRY_CODE = Config.getDefaultCountry();
    protected static final String INVALID_CITY = Config.getInvalidCity();
    protected static final String CITY_WITH_SPECIAL_CHARACTERS = "São Paulo";
    protected static final int SUCCESSFUL_STATUS = 200;
    protected static final int CITY_NOT_FOUND_STATUS = 404;
    protected static final String CITY_NOT_FOUND_MESSAGE_CONTAINS = "not found";
    protected static final int INVALID_API_KEY_STATUS = 401;
    protected static final String INVALID_API_KEY_MESSAGE_CONTAINS = "Invalid API key";
    protected final static String EMPTY_API_KEY = "";

    @BeforeClass
    public void setupClass() {
        logger.info("Setting up test class: {}", this.getClass().getSimpleName());

        // Initialize API client with configuration
        weatherApiClient = new WeatherApiClient(Config.getAllProperties());

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

    protected void logTestCompletion(String testName, boolean passed) {
        logger.info("Test '{}' completed. Status: {}", testName, passed ? "PASSED" : "FAILED");
    }

    protected void validateSuccessfulResponse(io.restassured.response.Response response) {
        response.then()
                .statusCode(SUCCESSFUL_STATUS)
                .header("Content-Type", containsString("application/json"));
    }

    protected void validateErrorResponse(Response response, int expectedStatus, String expectedMessageContains) {
        response.then()
                .statusCode(expectedStatus)
                .body("message", containsString(expectedMessageContains));
    }

    public void validateStandardWeatherResponse(Response response, String cityName) {
        validateSuccessfulResponse(response);
        response.then()
                .body("name", equalTo(cityName))
                .body("cod", equalTo(200))
                .body("main", notNullValue())
                .body("main.temp", notNullValue())
                .body("main.humidity", notNullValue())
                .body("weather", not(empty()))
                .body("weather[0].main", notNullValue())
                .body("weather[0].description", notNullValue());
    }

    protected void validateCoordinateSpecificWeatherResponse(Response response) {
        validateSuccessfulResponse(response);
        response.then()
                .body("coord", notNullValue())
                .body("name", notNullValue())
                .body("main.temp", notNullValue());
    }
}