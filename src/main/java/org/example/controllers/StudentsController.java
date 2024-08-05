package org.example.controllers;

import org.example.entities.Student;
import org.example.models.interfaces.IStudentsModel;

import java.util.Optional;

public class StudentsController {

    private final IStudentsModel studentsModel;

    public StudentsController(IStudentsModel studentsModel) {
        this.studentsModel = studentsModel;
    }


    public Student register(Student baseStudent) {
        return this.studentsModel.register(baseStudent);
    }

    public Optional<Student> findByEmail(String studentEmail) {
        return this.studentsModel.findByEmail(studentEmail);
    }

    public Optional<Student> findById(int studentId) {
        return this.studentsModel.findById(studentId);
    }

    public boolean update(int studentId, Student studentToUpdate) {
        return this.studentsModel.update(studentId, studentToUpdate);
    }

}
