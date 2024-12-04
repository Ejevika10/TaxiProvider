package com.modsen.driverservice.e2e;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@CucumberOptions(
        features = {"src/test/resources/com.modsen.driverservice.e2e/driver.feature",
                "src/test/resources/com.modsen.driverservice.e2e/car.feature"}
)
public class CucumberTest {
}