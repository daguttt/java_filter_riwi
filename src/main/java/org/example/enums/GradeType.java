package org.example.enums;

import java.util.Arrays;
import java.util.List;

public enum GradeType {
    WORKSHOP,
    QUIZ,
    ASSESSMENT;

    public static List<String> getGradeTypesAsStringList() {
        return Arrays.stream(GradeType.values()).map(Enum::name).toList();
    }
}
