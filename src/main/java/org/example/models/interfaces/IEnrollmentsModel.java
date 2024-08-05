package org.example.models.interfaces;

import org.example.entities.Enrollment;

import java.util.List;

public interface IEnrollmentsModel {
    List<Enrollment> findAllByStudentId(int studentIdQuery);
}
