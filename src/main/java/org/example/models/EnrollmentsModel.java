package org.example.models;

import org.example.entities.Enrollment;
import org.example.models.interfaces.IEnrollmentsModel;
import org.example.persistence.Database;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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

    @Override
    public Optional<Enrollment> findByStudentIdAndCourseId(int studentIdQuery, int courseIdQuery) {
        var connection = database.openConnection();
        var sql = """
                SELECT id, date, student_id, course_id\s
                FROM enrollments WHERE student_id = ? AND course_id = ?
                """;

        Optional<Enrollment> enrollment = Optional.empty();
        try(var statement = connection.prepareStatement(sql)) {
            statement.setInt(1, courseIdQuery);
            statement.setInt(2, studentIdQuery);

            var resultSet = statement.executeQuery();
            if (resultSet.next()) {
                var id = resultSet.getInt("id");
                var date = resultSet.getTimestamp("date");
                var studentId = resultSet.getInt("student_id");
                var courseId = resultSet.getInt("course_id");
                enrollment = Optional.of(new Enrollment(id, date, studentId, courseId));
            }
            resultSet.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            database.closeConnection();
        }

        return enrollment;

    }

    @Override
    public Enrollment create(Enrollment baseEnrollment) {
        var connection = database.openConnection();
        var sql = """
                INSERT INTO enrollments (date, student_id, course_id)\s
                    VALUES (?, ?, ?);
                """;

        try (var statement = connection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            statement.setTimestamp(1, baseEnrollment.getDate());
            statement.setInt(2, baseEnrollment.getStudentId());
            statement.setInt(3, baseEnrollment.getCourseId());

            statement.execute();

            var resultSet = statement.getGeneratedKeys();

            if (resultSet.next()) {
                var givenEnrollmentId = resultSet.getInt(1);
                baseEnrollment.setId(givenEnrollmentId);
            } else throw new SQLException("Couldn't create enrollment");

            resultSet.close();

        }
        catch(SQLException e) {
            throw new RuntimeException(e);
        } finally {
            database.closeConnection();
        }
        return baseEnrollment;

    }

    @Override
    public boolean delete(int enrollmentId) {
        var connection = database.openConnection();
        var sql = """
                DELETE FROM enrollments\s
                WHERE id = ?;
               """;

        try (var statement = connection.prepareStatement(sql)) {
            statement.setInt(1, enrollmentId);

            var affectedRows = statement.executeUpdate();
            statement.close();

            if (affectedRows == 1) return true;

        } catch (SQLException e) {
            System.out.printf("Error deleting enrollment (Error %s): %s%nenrollmentId: %d", e.getClass(), e.getMessage(), enrollmentId);
            return false;
        } finally {
            database.closeConnection();
        }

        return false;

    }
}
