package org.example.entities;

public class Student {
    private int id;
    private String name;
    private String email;
    private boolean isActive;

    public Student(int id, String name, String email, boolean isActive) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.isActive = isActive;
    }

    public Student(String name, String email, boolean isActive) {
        this.name = name;
        this.email = email;
        this.isActive = isActive;
    }

    public Student(String name, String email) {
        this.name = name;
        this.email = email;
        this.isActive = true;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
                String.format("Nombre: %s", getName()),
                String.format("Email: %s", getEmail()),
                String.format("¿Activo?: %s", isActive() ? "sí": "no"),
        };
        return String.join("\n", lines);
    }
}
