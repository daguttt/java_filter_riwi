package org.example.controllers;

import org.example.entities.Enrollment;
import org.example.models.interfaces.IEnrollmentsModel;

import java.util.List;
import java.util.Optional;

public class EnrollmentsController {
    private final IEnrollmentsModel enrollmentsModel;

    public EnrollmentsController(IEnrollmentsModel enrollmentsModel) {
        this.enrollmentsModel = enrollmentsModel;
    }

    public List<Enrollment> findAllByStudentId(int studentIdQuery) {
        return this.enrollmentsModel.findAllByStudentId(studentIdQuery);
    }

    public Enrollment create(Enrollment baseEnrollment) {
        return this.enrollmentsModel.create(baseEnrollment);
    }

    public Optional<Enrollment> findByStudentIdAndCourseId(int studentIdQuery, int courseIdQuery) {
        return this.enrollmentsModel.findByStudentIdAndCourseId(courseIdQuery, studentIdQuery);
    }

    public boolean delete(int enrollmentId) {
        return this.enrollmentsModel.delete(enrollmentId);
    }
}
