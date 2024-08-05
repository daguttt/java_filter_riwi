package org.example.utils;

import javax.swing.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class InputRequester {
    private final static String DEFAULT_INVALID_INPUT_MESSAGE = "Entrada inválida. Inténtalo de nuevo";
    private final static String DEFAULT_INVALID_INPUT_PANE_TITLE = "Entrada inválida";

    // String
    public static String requestString(String prompt) {
        return requestString(prompt, DEFAULT_INVALID_INPUT_MESSAGE, false);
    }

    public static String requestString(String prompt, boolean allowEmpty) {
        return requestString(prompt, DEFAULT_INVALID_INPUT_MESSAGE, allowEmpty);
    }

    public static String requestString(String prompt, String invalidInputMessage, boolean allowEmpty) {
        while (true) {
            String inputString = JOptionPane.showInputDialog(null, prompt);
            String trimmedInput = inputString == null ? "" : inputString.trim();
            if (allowEmpty) return trimmedInput;

            if (!trimmedInput.isEmpty()) return trimmedInput;

            JOptionPane.showMessageDialog(null, invalidInputMessage, DEFAULT_INVALID_INPUT_PANE_TITLE, JOptionPane.WARNING_MESSAGE);
        }
    }

    // LocalDate
    public static Optional<LocalDate> requestLocalDate(String prompt) {
        return requestLocalDate(prompt, "Formato de fecha inválido. Inténtalo de nuevo", false);
    }

    public static Optional<LocalDate> requestLocalDate(String prompt, boolean allowEmpty) {
        return requestLocalDate(prompt, "Formato de fecha inválido. Inténtalo de nuevo", allowEmpty);
    }

    public static Optional<LocalDate> requestLocalDate(String prompt, String invalidInputMessage) {
        return requestLocalDate(prompt, invalidInputMessage, false);
    }

    public static Optional<LocalDate> requestLocalDate(String prompt, String invalidInputMessage, boolean allowEmpty) {
        while (true) {
            String inputLocalDate = requestString(prompt + "\nFormat: YYYY-MM-DD", allowEmpty);
            if (allowEmpty && inputLocalDate.isEmpty()) return Optional.empty();

            var localDate = DateStringParser.parseLocalDate(inputLocalDate);
            if (localDate.isPresent()) return localDate;
            JOptionPane.showMessageDialog(null, invalidInputMessage, DEFAULT_INVALID_INPUT_PANE_TITLE, JOptionPane.WARNING_MESSAGE);
        }

    }

    // LocalDateTime
    public static Optional<LocalDateTime> requestLocalDateTime(String prompt, String invalidInputMessage) {
        return requestLocalDateTime(prompt, invalidInputMessage, false);
    }

    public static Optional<LocalDateTime> requestLocalDateTime(String prompt, boolean allowEmpty) {
        return requestLocalDateTime(prompt, "Formato de fecha y hora inválido. Inténtalo de nuevo", allowEmpty);
    }

    public static Optional<LocalDateTime> requestLocalDateTime(String prompt) {
        return requestLocalDateTime(prompt, "Formato de fecha y hora inválido. Inténtalo de nuevo", false);
    }

    public static Optional<LocalDateTime> requestLocalDateTime(String prompt, String invalidInputMessage, boolean allowEmpty) {
        while (true) {
            String inputLocalDateTime = requestString(prompt + "\nFormat: YYYY-MM-DDThh:mm:ss", allowEmpty);
            if (allowEmpty && inputLocalDateTime.isEmpty()) return Optional.empty();

            var localDateTime = DateStringParser.parseLocalDateTime(inputLocalDateTime);
            if (localDateTime.isPresent()) return localDateTime;
            JOptionPane.showMessageDialog(null, invalidInputMessage, DEFAULT_INVALID_INPUT_PANE_TITLE, JOptionPane.WARNING_MESSAGE);
        }
    }

    // LocalTime
    public static Optional<LocalTime> requestLocalTime(String prompt, String invalidInputMessage) {
        return requestLocalTime(prompt, invalidInputMessage, false);
    }

    public static Optional<LocalTime> requestLocalTime(String prompt, boolean allowEmpty) {
        return requestLocalTime(prompt, "Formato de hora inválido. Inténtalo de nuevo", allowEmpty);
    }

    public static Optional<LocalTime> requestLocalTime(String prompt) {
        return requestLocalTime(prompt, "Formato de hora inválido. Inténtalo de nuevo", false);
    }

    public static Optional<LocalTime> requestLocalTime(String prompt, String invalidInputMessage, boolean allowEmpty) {
        while (true) {
            String inputLocalTime = requestString(prompt + "\nFormat: hh:mm[:ss]", allowEmpty);
            if (allowEmpty && inputLocalTime.isEmpty()) return Optional.empty();

            var localTime = TimeStringParser.parseLocalTime(inputLocalTime);
            if (localTime.isPresent()) return localTime;
            JOptionPane.showMessageDialog(null, invalidInputMessage, DEFAULT_INVALID_INPUT_PANE_TITLE, JOptionPane.WARNING_MESSAGE);
        }
    }

    // Integer
    public static Optional<Integer> requestInteger(String prompt, String invalidInputMessage) {
        return requestInteger(prompt, invalidInputMessage, false);
    }

    public static Optional<Integer> requestInteger(String prompt, boolean allowEmpty) {
        return requestInteger(prompt, "La entrada no es un entero. Inténtalo de nuevo", allowEmpty);
    }

    public static Optional<Integer> requestInteger(String prompt) {
        return requestInteger(prompt, "La entrada no es un entero. Inténtalo de nuevo", false);
    }

    public static Optional<Integer> requestInteger(String prompt, String invalidInputMessage, boolean allowEmpty) {
        while (true) {
            String inputInteger = requestString(prompt, allowEmpty);
            if (allowEmpty && inputInteger.isEmpty()) return Optional.empty();

            try {
                return Optional.of(Integer.parseInt(inputInteger));
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(null, invalidInputMessage, DEFAULT_INVALID_INPUT_PANE_TITLE, JOptionPane.WARNING_MESSAGE);
            }
        }
    }

    // Double
    public static Optional<Double> requestDouble(String prompt, String invalidInputMessage) {
        return requestDouble(prompt, invalidInputMessage, false);
    }

    public static Optional<Double> requestDouble(String prompt, boolean allowEmpty) {
        return requestDouble(prompt, "La entrada no es un número válido. Inténtalo de nuevo", allowEmpty);
    }

    public static Optional<Double> requestDouble(String prompt) {
        return requestDouble(prompt, "La entrada no es un número válido. Inténtalo de nuevo", false);
    }

    public static Optional<Double> requestDouble(String prompt, String invalidInputMessage, boolean allowEmpty) {
        while (true) {
            String inputDouble = requestString(prompt, allowEmpty);
            if (allowEmpty && inputDouble.isEmpty()) return Optional.empty();

            try {
                return Optional.of(Double.parseDouble(inputDouble));
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(null, invalidInputMessage, DEFAULT_INVALID_INPUT_PANE_TITLE, JOptionPane.WARNING_MESSAGE);
            }
        }
    }

    // Index from a string list
    public static int requestAnIndexFrom(List<String> optionsList, String prompt) {
        return requestAnIndexFrom(optionsList, prompt, "Número de opción inválida. Inténtalo de nuevo");
    }

    public static int requestAnIndexFrom(List<String> optionsList, String prompt, String invalidInputMessage) {
        var formattedOptions = new ArrayList<String>();
        for (int i = 0; i < optionsList.size(); i++) {
            String option = optionsList.get(i);
            var listedOption = String.format("%d. %s", i + 1, option);
            formattedOptions.add(listedOption);
        }

        while (true) {
            var chosenOption = requestInteger(prompt + "\n" + String.join("\n", formattedOptions));
            if (chosenOption.isEmpty()) return -1;

            boolean isValidOption = chosenOption.get() > 0 && chosenOption.get() <= optionsList.size();
            if (isValidOption) return chosenOption.get() - 1;
            JOptionPane.showMessageDialog(null, invalidInputMessage, DEFAULT_INVALID_INPUT_PANE_TITLE, JOptionPane.WARNING_MESSAGE);
        }
    }
}
