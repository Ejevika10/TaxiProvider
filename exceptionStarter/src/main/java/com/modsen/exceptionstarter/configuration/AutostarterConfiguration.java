package com.modsen.exceptionstarter.configuration;

import com.modsen.exceptionstarter.GlobalExceptionHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class AutostarterConfiguration {

    private final MessageSource messageSource;

    @Bean
    public GlobalExceptionHandler globalExceptionHandler() {
        return new GlobalExceptionHandler(messageSource);
    }
}
