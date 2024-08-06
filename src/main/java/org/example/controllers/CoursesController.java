package org.example.controllers;

import org.example.entities.Course;
import org.example.models.interfaces.ICoursesModel;

import java.util.List;
import java.util.Optional;

public class CoursesController {
    private final ICoursesModel coursesModel;

    public CoursesController(ICoursesModel coursesModel) {
        this.coursesModel = coursesModel;
    }

    public Course create(Course baseCourse) {
        return this.coursesModel.create(baseCourse);
    }

    public Optional<Course> findByName(String nameQuery) {
        return this.coursesModel.findByName(nameQuery);
    }

    public boolean delete(int courseId) {
        return this.coursesModel.delete(courseId);
    }

    public List<Course> findAll() {
        return this.coursesModel.findAll();
    }

    public Optional<Course> findById(int courseIdQuery) {
        return this.coursesModel.findById(courseIdQuery);
    }
}
