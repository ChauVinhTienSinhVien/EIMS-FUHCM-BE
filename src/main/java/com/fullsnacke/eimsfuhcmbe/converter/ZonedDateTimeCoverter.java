package com.fullsnacke.eimsfuhcmbe.converter;

import jakarta.persistence.AttributeConverter;

import java.time.ZoneId;
import java.time.ZonedDateTime;

public class ZonedDateTimeCoverter implements AttributeConverter<ZonedDateTime, ZonedDateTime> {
    private static final ZoneId UTC = ZoneId.of("UTC");
    private static final ZoneId DEFAULT_ZONE = ZoneId.systemDefault();

    @Override
    public ZonedDateTime convertToDatabaseColumn(ZonedDateTime zonedDateTime) {
        if (zonedDateTime == null) {
            return null;
        }
        ZonedDateTime utc7DateTime = zonedDateTime.withZoneSameInstant(DEFAULT_ZONE);
        return utc7DateTime.withZoneSameInstant(UTC);
    }

    @Override
    public ZonedDateTime convertToEntityAttribute(ZonedDateTime zonedDateTime) {
        if (zonedDateTime == null) {
            return null;
        }

        ZonedDateTime utc7DateTime = zonedDateTime.withZoneSameInstant(UTC);
        System.out.println("utc7DateTime: " + utc7DateTime);
        System.out.println("utc7DateTime.withZoneSameInstant(DEFAULT_ZONE): " + utc7DateTime.withZoneSameInstant(DEFAULT_ZONE));
        return utc7DateTime.withZoneSameInstant(DEFAULT_ZONE);
    }
}
