package org.example.utils;

import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.Optional;

public class TimeStringParser {
    public static Optional<LocalTime> parseLocalTime(String dateTimeString) {
        try {
            var localTime = LocalTime.parse(dateTimeString);
            return Optional.of(localTime);
        } catch (DateTimeParseException e) {
            return Optional.empty();
        }
    }
}
