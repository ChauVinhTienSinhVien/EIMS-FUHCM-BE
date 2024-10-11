package com.fullsnacke.eimsfuhcmbe.converter;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;

@Converter(autoApply = true)
public class InstantConverter implements AttributeConverter<Instant, Instant> {
    private static final ZoneId UTC = ZoneId.of("UTC");
    private static final ZoneId DEFAULT_ZONE = ZoneId.systemDefault();

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
        ZonedDateTime utcDateTime = dbInstant.atZone(UTC);
        ZonedDateTime defaultZoneDateTime = utcDateTime.withZoneSameInstant(DEFAULT_ZONE);
        return defaultZoneDateTime.toInstant();
    }
}
