# weather-api-test-framework
Scalable, maintainable API test framework that incorporates modern day tooling to showcase API testing

1. Create a `config` package in `src/test/resources`
2. Add a `test.properties` file in the `config` package
3. Add the following properties to the `test.properties` file
    1. `api.key=YOURAPIKEY`
    2. `api.base.url=https://api.openweathermap.org/data/2.5`
    3. Get your free API Key at: https://openweathermap.org/'

Add a test.properties file in src/test/resources/config and include  
api.key=YOURAPIKEY  
Get your free API Key at https://openweathermap.org/

Run with the following:  

``` shell
   mvn clean test-compile  
```

``` shell
   mvn clean test; 
   mvn allure:serv;
```
