package org.example.entities;

public class Student {
    private int id;
    private String fullName;
    private String email;
    private boolean isActive;

    public Student(int id, String fullName, String email, boolean isActive) {
        this.id = id;
        this.fullName = fullName;
        this.email = email;
        this.isActive = isActive;
    }

    public Student(String fullName, String email, boolean isActive) {
        this.fullName = fullName;
        this.email = email;
        this.isActive = isActive;
    }

    public Student(String fullName, String email) {
        this.fullName = fullName;
        this.email = email;
        this.isActive = true;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    @Override
    public String toString() {
        var lines = new String[] {
                String.format("ID: %d", getId()),
                String.format("Nombre completo: %s", getFullName()),
                String.format("Email: %s", getEmail()),
                String.format("¿Activo?: %s", isActive() ? "Sí": "No"),
        };
        return String.join("\n", lines);
    }
}
