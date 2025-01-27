package com.modsen.ratingservice.configuration;

import com.modsen.ratingservice.util.StringToUUIDConverter;
import com.modsen.ratingservice.util.UUIDToStringConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.convert.MongoCustomConversions;

import java.util.Arrays;

@Configuration
public class MongoConfiguration {
    @Bean
    public MongoCustomConversions customConversions() {
        return new MongoCustomConversions(Arrays.asList(
                new UUIDToStringConverter(),
                new StringToUUIDConverter()
        ));
    }
}
