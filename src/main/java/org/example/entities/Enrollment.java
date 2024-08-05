package org.example.entities;

import java.sql.Timestamp;

public class Enrollment {
    private int id;
    private Timestamp date;
    private int userId;
    private int courseId;

    public Enrollment(int id, Timestamp date, int userId, int courseId) {
        this.id = id;
        this.date = date;
        this.userId = userId;
        this.courseId = courseId;
    }

    public Enrollment(Timestamp date, int userId, int courseId) {
        this.date = date;
        this.userId = userId;
        this.courseId = courseId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Timestamp getDate() {
        return date;
    }

    public void setDate(Timestamp date) {
        this.date = date;
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
                String.format("Fecha inscripción: %1$Te/%1$Tm/%1$TY %1$Tr", getDate()),
                String.format("ID Estudiante: %s", getUserId()),
                String.format("ID Curso: %s", getCourseId()),
        };
        return String.join("\n", lines);

    }
}
