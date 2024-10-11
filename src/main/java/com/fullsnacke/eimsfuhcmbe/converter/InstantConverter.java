package com.fullsnacke.eimsfuhcmbe.converter;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

@Converter(autoApply = true)
public class InstantConverter implements AttributeConverter<Instant, Instant> {
    private static final ZoneId UTC = ZoneId.of("UTC");
    private static final ZoneId DEFAULT_ZONE = ZoneId.systemDefault();
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ISO_ZONED_DATE_TIME;

    @Override
    public Instant convertToDatabaseColumn(Instant instant) {
        if (instant == null) {
            return null;
        }
        ZonedDateTime utc7DateTime = instant.atZone(DEFAULT_ZONE);
        ZonedDateTime utcDateTime = utc7DateTime.withZoneSameInstant(UTC);
        return utcDateTime.toInstant();
    }

    @Override
    public Instant convertToEntityAttribute(Instant dbInstant) {
        if (dbInstant == null) {
            return null;
        }
        System.out.println("dbInstant: " + dbInstant);
        ZonedDateTime utcDateTime = dbInstant.atZone(UTC);
        ZonedDateTime defaultZoneDateTime = utcDateTime.withZoneSameInstant(DEFAULT_ZONE);
        System.out.println("defaultZoneDateTime: " + defaultZoneDateTime);
        System.out.println("defaultZoneDateTime.toInstant()" + defaultZoneDateTime.toInstant());
        return defaultZoneDateTime.toInstant();
    }
}
