package com.weatherapi.tests.functional;

import com.weatherapi.core.BaseTest;
import com.weatherapi.models.WeatherResponse;
import io.qameta.allure.*;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.Test;

import static org.hamcrest.Matchers.*;

@Epic("Weather API Testing")
@Feature("Weather Data Functional")
public class WeatherFunctionalTests extends BaseTest {

    @Test(description = "Verify successful weather data retrieval for a valid city")
    @Story("Valid city weather retrieval")
    @Severity(SeverityLevel.CRITICAL)
    public void testGetWeatherForValidCity() {
        Response response = weatherApiClient.getCurrentWeather(DEFAULT_CITY, DEFAULT_COUNTRY_CODE);
        Allure.addAttachment("API Response", response.getBody().asString());
        validateStandardWeatherResponse(response, DEFAULT_CITY);
        logger.info("Weather data successfully retrieved for: {}, {}", DEFAULT_CITY, DEFAULT_COUNTRY_CODE);
        logTestCompletion("testGetWeatherForValidCity", true);
    }

    @Test(description = "Verify API handles special characters in city name")
    @Story("Special Characters")
    @Severity(SeverityLevel.NORMAL)
    public void testGetWeatherWithSpecialCharactersInCityName() {
        Response response = weatherApiClient.getCurrentWeather(CITY_WITH_SPECIAL_CHARACTERS, null);
        Allure.addAttachment("City with special characters response: ", response.getBody().asString());
        validateStandardWeatherResponse(response, CITY_WITH_SPECIAL_CHARACTERS);
        logger.info("City with special characters response passed for city: {}", CITY_WITH_SPECIAL_CHARACTERS);
        logTestCompletion("testGetWeatherWithSpecialCharactersInCityName", true);
    }

    @Test(description = "Verify API handles invalid city gracefully")
    @Story("Error handling for invalid city")
    @Severity(SeverityLevel.NORMAL)
    public void testGetWeatherForInvalidCity() {
        Response response = weatherApiClient.getCurrentWeather(INVALID_CITY, null);
        Allure.addAttachment("Error Response", response.getBody().asString());
        validateErrorResponse(response, CITY_NOT_FOUND_STATUS, CITY_NOT_FOUND_MESSAGE_CONTAINS);
        logger.info("Invalid city test completed - API correctly returned HTTP STATUS {} for: {}", CITY_NOT_FOUND_STATUS, INVALID_CITY);
        logTestCompletion("testGetWeatherForInvalidCity", true);
    }

    @Test(description = "Verify weather data by coordinates")
    @Story("Coordinate-based weather retrieval")
    @Severity(SeverityLevel.NORMAL)
    public void testGetWeatherByCoordinates() {
        // London coordinates
        double lat = 51.5074;
        double lon = -0.1278;

        Response response = weatherApiClient.getCurrentWeatherByCoordinates(lat, lon);
        Allure.addAttachment("Coordinate Response", response.getBody().asString());

        // Validate response
        validateSuccessfulResponse(response);

        // Validate coordinate-specific response
        response.then()
                .body("coord", notNullValue())
                .body("name", notNullValue())
                .body("main.temp", notNullValue());

        // Extract and validate coordinates with proper type handling
        float actualLat = response.jsonPath().getFloat("coord.lat");
        float actualLon = response.jsonPath().getFloat("coord.lon");

        Assert.assertTrue(Math.abs(actualLat - lat) < 1.0,
                String.format("Latitude should be close to expected. Expected: %.4f, Actual: %.4f", lat, actualLat));
        Assert.assertTrue(Math.abs(actualLon - lon) < 1.0,
                String.format("Longitude should be close to expected. Expected: %.4f, Actual: %.4f", lon, actualLon));

        String cityName = response.jsonPath().getString("name");
        logger.info("Weather data retrieved by coordinates ({}, {}): City identified as {}",
                lat, lon, cityName);

        logTestCompletion("testGetWeatherByCoordinates", true);
    }

    public void validateStandardWeatherResponse(Response response, String cityName) {
        response.then()
                .body("name", equalTo(cityName))
                .body("cod", equalTo(200))
                .body("main", notNullValue())
                .body("main.temp", notNullValue())
                .body("main.humidity", notNullValue())
                .body("weather", not(empty()))
                .body("weather[0].main", notNullValue())
                .body("weather[0].description", notNullValue())
                .statusCode(200)
                .header("Content-Type", org.hamcrest.Matchers.containsString("application/json"));
    }
}