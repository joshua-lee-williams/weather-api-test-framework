# weather-api-test-framework
Scalable, maintainable API test framework that incorporates modern day tooling to showcase API testing

Add a test.properties file in src/test/resources/config and include  
api.key=YOURAPIKEY  
Get your free API Key at https://openweathermap.org/

Run with the following:  
mvn clean test-compile  
mvn clean test; mvn allure:serv