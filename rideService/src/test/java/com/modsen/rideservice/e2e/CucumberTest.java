package com.modsen.rideservice.e2e;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@CucumberOptions(
        features = {"src/test/resources/com.modsen.rideservice.e2e/ride.feature"}
)
public class CucumberTest {
}