package com.fullsnacke.eimsfuhcmbe.util;


import java.time.Instant;


public class DateValidationUtil {

    /**
     * Check if the current time is within the given time range.
     */
    public static boolean isWithinTimeRange(Instant startTime, Instant endTime) {
        Instant now = Instant.now();
        return (now.isAfter(startTime) && now.isBefore(endTime));
    }

    /**
     * Check if the current time is before a specific deadline.
     */
    public static boolean isBeforeDeadline(Instant deadline) {
        return Instant.now().isBefore(deadline);
    }

    /**
     * Check if the current time is after a specific time limit.
     */
    public static boolean isAfterTimeLimit(Instant timeLimit) {
        return Instant.now().isAfter(timeLimit);
    }

}
