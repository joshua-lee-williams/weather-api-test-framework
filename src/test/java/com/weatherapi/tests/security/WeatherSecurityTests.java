package com.weatherapi.tests.security;

import com.weatherapi.core.BaseTest;
import io.qameta.allure.*;
import io.restassured.response.Response;
import org.testng.annotations.Test;

@Epic("Weather API Testing")
@Feature("Weather Data Security")
public class WeatherSecurityTests extends BaseTest {

    @Test(description = "Verify empty API Key is handled correctly")
    @Story("Performance validation")
    @Severity(SeverityLevel.NORMAL)
    public void testGetWeatherWithEmptyApiKey() {
        Response response = weatherApiClient.getCurrentWeather(DEFAULT_CITY, DEFAULT_COUNTRY_CODE, EMPTY_API_KEY);
        validateErrorResponse(response, INVALID_API_KEY_STATUS, INVALID_API_KEY_MESSAGE_CONTAINS);
        logger.info("Empty API Key test completed - API correctly returned HTTP Status {} for apiKey: {}", INVALID_API_KEY_STATUS, EMPTY_API_KEY);
        logTestCompletion("testGetWeatherWithEmptyApiKey", true);
    }

}