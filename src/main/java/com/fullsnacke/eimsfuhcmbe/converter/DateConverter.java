package com.fullsnacke.eimsfuhcmbe.converter;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;

@Converter(autoApply = true)
public class DateConverter implements AttributeConverter<Date, Date> {
    private static final ZoneId UTC = ZoneId.of("UTC");
    private static final ZoneId DEFAULT_ZONE = ZoneId.of("Asia/Bangkok");

    @Override
    public Date convertToDatabaseColumn(Date date) {
        if (date == null) {
            return null;
        }
        Instant instant = date.toInstant();
        ZonedDateTime utc7DateTime = instant.atZone(DEFAULT_ZONE);
        ZonedDateTime utcDateTime = utc7DateTime.withZoneSameInstant(UTC);
        return Date.from(utcDateTime.toInstant());
    }

    @Override
    public Date convertToEntityAttribute(Date dbDate) {
        if (dbDate == null) {
            return null;
        }
        Instant instant = dbDate.toInstant();
        ZonedDateTime utcDateTime = instant.atZone(UTC);
        ZonedDateTime utc7DateTime = utcDateTime.withZoneSameInstant(DEFAULT_ZONE);
        return Date.from(utc7DateTime.toInstant());
    }
}