
package com.weatherapi.core;

import com.weatherapi.config.TestConfig;
import com.weatherapi.models.WeatherResponse;
import io.restassured.RestAssured;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

public class WeatherApiClient {
    private static final Logger logger = LoggerFactory.getLogger(WeatherApiClient.class);

    private final String baseUrl;
    private final String apiKey;
    private final int timeout;

    public WeatherApiClient(Properties config) {
        this.baseUrl = config.getProperty("api.base.url");
        this.apiKey = TestConfig.getApiKey();
        this.timeout = Integer.parseInt(config.getProperty("api.timeout", "5000"));

        // Configure REST Assured defaults
        RestAssured.baseURI = baseUrl;
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();

        // Add detailed logging to see what's being sent
        // RestAssured.filters(new RequestLoggingFilter(), new ResponseLoggingFilter());

    }

    /**
     * Get weather data for a city
     * @param city City name (e.g., "London")
     * @param countryCode Optional country code (e.g., "UK")
     * @return REST Assured Response object
     */
    public Response getCurrentWeather(String city, String countryCode) {
        String location = countryCode != null ? city + "," + countryCode : city;

        logger.info("Getting weather data for: {}", location);

        RequestSpecification request = RestAssured.given()
                .queryParam("q", location)
                .queryParam("appid", apiKey)
                .queryParam("units", "metric"); // Celsius

        Response response = request
                .when()
                .get("/weather")
                .then()
                .extract()
                .response();

        logger.info("API Response Status: {}", response.getStatusCode());
        return response;
    }

    /**
     * Get weather data by coordinates
     * @param lat Latitude
     * @param lon Longitude
     * @return REST Assured Response object
     */
    public Response getCurrentWeatherByCoordinates(double lat, double lon) {
        logger.info("Getting weather data for coordinates: {}, {}", lat, lon);

        RequestSpecification request = RestAssured.given()
                .queryParam("lat", lat)
                .queryParam("lon", lon)
                .queryParam("appid", apiKey)
                .queryParam("units", "metric");

        return request
                .when()
                .get("/weather")
                .then()
                .extract()
                .response();
    }

    /**
     * Get weather data and deserialize to POJO
     * @param city City name
     * @param countryCode Optional country code
     * @return WeatherResponse object
     */
    public WeatherResponse getCurrentWeatherAsObject(String city, String countryCode) {
        Response response = getCurrentWeather(city, countryCode);

        if (response.getStatusCode() == 200) {
            return response.as(WeatherResponse.class);
        } else {
            logger.error("Failed to get weather data. Status: {}, Body: {}",
                    response.getStatusCode(), response.getBody().asString());
            throw new RuntimeException("Weather API request failed with status: " + response.getStatusCode());
        }
    }

    /**
     * Validate API key by making a simple request
     * @return true if API key is valid
     */
    public boolean validateApiKey() {
        try {
            logger.info("Attempted API Key: {}", apiKey);
            Response response = getCurrentWeather("London", "UK");
            return response.getStatusCode() != 401;
        } catch (Exception e) {

            logger.error("API key validation failed", e);
            return false;
        }
    }
}