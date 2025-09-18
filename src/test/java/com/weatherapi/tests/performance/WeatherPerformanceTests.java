package com.weatherapi.tests.performance;

import com.weatherapi.core.BaseTest;
import io.qameta.allure.*;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.Test;

@Epic("Weather API Testing")
@Feature("Weather Data Performance")
public class WeatherPerformanceTests extends BaseTest {

    @Test(description = "Verify response time is acceptable")
    @Story("Performance validation")
    @Severity(SeverityLevel.NORMAL)
    public void testWeatherApiResponseTime() {
        // Measure response time
        long startTime = System.currentTimeMillis();
        Response response = weatherApiClient.getCurrentWeather(DEFAULT_CITY, DEFAULT_COUNTRY_CODE);
        long responseTime = System.currentTimeMillis() - startTime;

        Allure.addAttachment("Response Time", responseTime + "ms");
        Allure.addAttachment("API Response", response.getBody().asString());

        validateSuccessfulResponse(response);

        Assert.assertTrue(responseTime < 3000,
                String.format("API response time (%dms) should be less than 3000ms", responseTime));

        logger.info("Response time for {}, {}: {}ms", DEFAULT_CITY, DEFAULT_COUNTRY_CODE, responseTime);
        logTestCompletion("testWeatherApiResponseTime", true);
    }

}