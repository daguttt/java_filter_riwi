package org.example.models.interfaces;

import org.example.entities.Grade;

import java.util.List;

public interface IGradesModel {
    boolean deleteAllByStudentIdAndCourseId(int studentId, int courseId);

    Grade create(Grade baseGrade);

    List<Grade> findAllByStudentIdAndCourseId(int studentIdQuery, int courseIdQuery);

    boolean update(int gradeId, Grade gradeToUpdate);

}
