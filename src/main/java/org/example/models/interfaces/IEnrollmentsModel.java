package org.example.models.interfaces;

import org.example.entities.Enrollment;

import java.util.List;
import java.util.Optional;

public interface IEnrollmentsModel {
    List<Enrollment> findAllByStudentId(int studentIdQuery);

    Optional<Enrollment> findByStudentIdAndCourseId(int courseIdQuery, int studentIdQuery);

    Enrollment create(Enrollment baseEnrollment);

    boolean delete(int enrollmentId);
}
