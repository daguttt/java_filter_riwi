package org.example.models.interfaces;

import org.example.entities.Course;

import java.util.List;
import java.util.Optional;

public interface ICoursesModel {
    Course create(Course baseCourse);

    Optional<Course> findByName(String nameQuery);

    boolean delete(int courseId);

    List<Course> findAll();
}
