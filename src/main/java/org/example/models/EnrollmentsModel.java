package org.example.models;

import org.example.entities.Enrollment;
import org.example.models.interfaces.IEnrollmentsModel;
import org.example.persistence.Database;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class EnrollmentsModel implements IEnrollmentsModel {
    private final Database database;

    public EnrollmentsModel(Database database) {
        this.database = database;
    }

    @Override
    public List<Enrollment> findAllByStudentId(int studentIdQuery) {
        var connection = database.openConnection();
        var sql = """
                SELECT id, date, student_id, course_id\s
                    FROM enrollments WHERE student_id = ?;
                """;

        var enrollmentList = new ArrayList<Enrollment>();

        try (var statement = connection.prepareStatement(sql)) {
            statement.setInt(1, studentIdQuery);

            var resultSet = statement.executeQuery();

            while (resultSet.next()) {
                var id = resultSet.getInt("id");
                var date = resultSet.getTimestamp("date");
                var studentId = resultSet.getInt("student_id");
                var courseId = resultSet.getInt("course_id");
                var enrollment = new Enrollment(id, date, studentId, courseId);
                enrollmentList.add(enrollment);
            }
            resultSet.close();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        database.closeConnection();
        return enrollmentList.stream().toList();

    }
}
