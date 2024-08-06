package org.example.controllers;

import org.example.entities.Grade;
import org.example.models.interfaces.IGradesModel;

import java.util.List;

public class GradesController {
    private final IGradesModel gradesModel;

    public GradesController(IGradesModel gradesModel) {
        this.gradesModel = gradesModel;
    }

    public boolean deleteAllByStudentIdAndCourseId(int studentId, int courseId) {
        return this.gradesModel.deleteAllByStudentIdAndCourseId(studentId, courseId);
    }

    public Grade create(Grade baseGrade) {
        return this.gradesModel.create(baseGrade);
    }

    public List<Grade> findAllByStudentIdAndCourseId(int studentIdQuery, int courseIdQuery) {
        return this.gradesModel.findAllByStudentIdAndCourseId(studentIdQuery, courseIdQuery);
    }

    public boolean update(int gradeInt, Grade gradeToUpdate) {
        return this.gradesModel.update(gradeInt, gradeToUpdate);
    }
}
