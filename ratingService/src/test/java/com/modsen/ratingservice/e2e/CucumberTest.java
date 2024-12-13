package com.modsen.ratingservice.e2e;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@CucumberOptions(
        features = {"src/test/resources/com/modsen/ratingservice/passengerrating.feature",
        "src/test/resources/com/modsen/ratingservice/driverrating.feature"}
)
public class CucumberTest {
}
