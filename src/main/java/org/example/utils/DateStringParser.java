package org.example.utils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.Optional;

public class DateStringParser {
    public static Optional<LocalDate> parseLocalDate(String dateString) {
        try {
            var localDate = LocalDate.parse(dateString);
            return Optional.of(localDate);
        } catch (DateTimeParseException e) {
            return Optional.empty();
        }
    }

    public static Optional<LocalDateTime> parseLocalDateTime(String dateTimeString) {
        try {
            var localDateTime = LocalDateTime.parse(dateTimeString);
            return Optional.of(localDateTime);
        } catch (DateTimeParseException e) {
            return Optional.empty();
        }
    }
}
