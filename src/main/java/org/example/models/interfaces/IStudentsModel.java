package org.example.models.interfaces;

import org.example.entities.Student;

import java.util.List;
import java.util.Optional;

public interface IStudentsModel {
    Student register(Student baseStudent);
    boolean update(int studentId, Student studentToUpdate);
    List<Student> findAllActive();
    Optional<Student> findById(int studentId);
    Optional<Student> findByEmail(String studentEmail);
}
