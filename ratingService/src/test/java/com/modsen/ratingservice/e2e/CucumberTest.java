package com.modsen.ratingservice.e2e;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@RunWith(Cucumber.class)
@CucumberOptions(
        features = {"src/test/resources/e2e/passengerrating.feature",
        "src/test/resources/e2e/driverrating.feature"}
)
@EnableAutoConfiguration(exclude={DataSourceAutoConfiguration.class})
public class CucumberTest {
}
