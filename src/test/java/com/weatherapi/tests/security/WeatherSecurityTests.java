package com.weatherapi.tests.security;

import com.weatherapi.core.BaseTest;
import io.qameta.allure.*;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.Test;

@Epic("Weather API Testing")
@Feature("Weather Data Security")
public class WeatherSecurityTests extends BaseTest {

    private final static String emptyAPIKey = "";

    @Test(description = "Verify empty API Key is handled correctly")
    @Story("Performance validation")
    @Severity(SeverityLevel.NORMAL)
    public void testGetWeatherWithEmptyApiKey() {
        // Measure response time
        long startTime = System.currentTimeMillis();
        Response response = weatherApiClient.getCurrentWeather(city, countryCode, emptyAPIKey);
        response.then()
                .statusCode(401);

        logger.info("Empty API Key test completed - API correctly returned 401 for apiKey: {}", emptyAPIKey);
        logTestCompletion("testGetWeatherWithEmptyApiKey", true);
    }

}