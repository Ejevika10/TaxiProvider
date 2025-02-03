package com.modsen.ratingservice.util;

import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.WritingConverter;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@WritingConverter
public class UUIDToStringConverter implements Converter<UUID, String> {
    @Override
    public String convert(UUID uuid) {
        return uuid.toString();
    }
}

