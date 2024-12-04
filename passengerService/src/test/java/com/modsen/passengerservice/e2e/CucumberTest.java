package com.modsen.passengerservice.e2e;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@CucumberOptions(
        features = {"src/test/resources/com.modsen.passengerservice.e2e/passenger.feature"}
)
public class CucumberTest {
}