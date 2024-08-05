package org.example.entities;

import org.example.enums.GradeType;

public class Grade {
    private int id;
    private int grade;
    private String description;
    private GradeType type;
    private int userId;
    private int courseId;

    public Grade(int id, int grade, String description, GradeType type, int userId, int courseId) {
        this.id = id;
        this.grade = grade;
        this.description = description;
        this.type = type;
        this.userId = userId;
        this.courseId = courseId;
    }

    public Grade(int grade, String description, GradeType type, int userId, int courseId) {
        this.grade = grade;
        this.description = description;
        this.type = type;
        this.userId = userId;
        this.courseId = courseId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getGrade() {
        return grade;
    }

    public void setGrade(int grade) {
        this.grade = grade;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public GradeType getType() {
        return type;
    }

    public void setType(GradeType type) {
        this.type = type;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getCourseId() {
        return courseId;
    }

    public void setCourseId(int courseId) {
        this.courseId = courseId;
    }

    @Override
    public String toString() {
        var lines = new String[] {
                String.format("ID: %d", getId()),
                String.format("Calificacion: %d", getGrade()),
                String.format("Descripción: %s", getDescription()),
                String.format("Tipo de calificación: %s", getType().name().toLowerCase()),
                String.format("ID Estudiante: %s", getUserId()),
                String.format("ID Curso: %s", getCourseId()),
        };
        return String.join("\n", lines);

    }
}

