package com.modsen.ratingservice.e2e;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@RunWith(Cucumber.class)
@CucumberOptions(
        features = {"src/test/resources/com/modsen/ratingservice/passengerrating.feature",
        "src/test/resources/com/modsen/ratingservice/driverrating.feature"}
)
@EnableAutoConfiguration(exclude={DataSourceAutoConfiguration.class})
public class CucumberTest {
}
