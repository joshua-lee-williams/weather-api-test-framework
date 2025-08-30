package com.weatherapi.tests.functional;

import com.weatherapi.core.BaseTest;
import com.weatherapi.models.WeatherResponse;
import io.qameta.allure.*;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.Test;

import static org.hamcrest.Matchers.*;

@Epic("Weather API Testing")
@Feature("Weather Data Retrieval")
public class WeatherRetrievalTests extends BaseTest {

    @Test(description = "Verify successful weather data retrieval for a valid city")
    @Story("Valid city weather retrieval")
    @Severity(SeverityLevel.CRITICAL)
    public void testGetWeatherForValidCity() {
        // Test data
        String city = "Foley";
        String countryCode = "US";

        // Execute API call
        Response response = weatherApiClient.getCurrentWeather(city, countryCode);
        Allure.addAttachment("API Response", response.getBody().asString());

        // Validate HTTP status and headers
        validateSuccessfulResponse(response);

        // Validate JSON response structure and data
        response.then()
                .body("name", equalTo(city))
                .body("cod", equalTo(200))
                .body("main", notNullValue())
                .body("main.temp", notNullValue())
                .body("main.humidity", notNullValue())
                .body("weather", not(empty()))
                .body("weather[0].main", notNullValue())
                .body("weather[0].description", notNullValue());

        // Additional validation using POJO
        WeatherResponse weatherData = response.as(WeatherResponse.class);
        Assert.assertEquals(weatherData.getName(), city, "City name should match request");
        Assert.assertEquals(weatherData.getCod(), 200, "Response code should be 200");
        Assert.assertNotNull(weatherData.getMain(), "Main weather data should not be null");
        Assert.assertTrue(weatherData.getMain().getHumidity() > 0, "Humidity should be greater than 0");

        // Log results for debugging
        logger.info("Weather data retrieved for {}: Temperature={}°C, Humidity={}%",
                city, weatherData.getMain().getTemp(), weatherData.getMain().getHumidity());

        logTestCompletion("testGetWeatherForValidCity", true);
    }

    @Test(description = "Verify API handles invalid city gracefully")
    @Story("Error handling for invalid city")
    @Severity(SeverityLevel.NORMAL)
    public void testGetWeatherForInvalidCity() {
        // Test data - using a clearly non-existent city
        String invalidCity = "NonExistentCity123";

        // Execute API call
        Response response = weatherApiClient.getCurrentWeather(invalidCity, null);
        Allure.addAttachment("Error Response", response.getBody().asString());

        // Validate error response
        response.then()
                .statusCode(404)
                .body("message", containsString("not found"));

        logger.info("Invalid city test completed - API correctly returned 404 for: {}", invalidCity);
        logTestCompletion("testGetWeatherForInvalidCity", true);
    }

    @Test(description = "Verify response time is acceptable")
    @Story("Performance validation")
    @Severity(SeverityLevel.NORMAL)
    public void testWeatherApiResponseTime() {
        String city = "Paris";

        // Measure response time
        long startTime = System.currentTimeMillis();
        Response response = weatherApiClient.getCurrentWeather(city, "FR");
        long responseTime = System.currentTimeMillis() - startTime;

        Allure.addAttachment("Response Time", responseTime + "ms");
        Allure.addAttachment("API Response", response.getBody().asString());

        // Validate response
        validateSuccessfulResponse(response);

        // Performance assertion - should respond within 3 seconds
        Assert.assertTrue(responseTime < 3000,
                String.format("API response time (%dms) should be less than 3000ms", responseTime));

        logger.info("Response time for {}: {}ms", city, responseTime);
        logTestCompletion("testWeatherApiResponseTime", true);
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
}