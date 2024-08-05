package org.example.controllers;

import org.example.entities.Enrollment;
import org.example.models.interfaces.IEnrollmentsModel;

import java.util.List;

public class EnrollmentsController {
    private IEnrollmentsModel enrollmentsModel;

    public EnrollmentsController(IEnrollmentsModel enrollmentsModel) {
        this.enrollmentsModel = enrollmentsModel;
    }

    public List<Enrollment> findAllByStudentId(int studentIdQuery) {
        return this.enrollmentsModel.findAllByStudentId(studentIdQuery);
    }
}
