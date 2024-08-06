package org.example.models;

import org.example.entities.Grade;
import org.example.enums.GradeType;
import org.example.models.interfaces.IGradesModel;
import org.example.persistence.Database;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class GradesModel implements IGradesModel {
    private final Database database;

    public GradesModel(Database database) {
        this.database = database;
    }


    @Override
    public boolean deleteAllByStudentIdAndCourseId(int studentId, int courseId) {
        var connection = database.openConnection();
        var sql = """
                DELETE FROM grades\s
                WHERE student_id = ? AND course_id = ?;
               """;

        try (var statement = connection.prepareStatement(sql)) {
            statement.setInt(1, studentId);
            statement.setInt(2, courseId);

            var affectedRows = statement.executeUpdate();
            statement.close();

            if (affectedRows == 1) return true;

        } catch (SQLException e) {
            System.out.printf("Error deleting grades (Error %s): %s%nstudentId: %d%ncourseId: %d", e.getClass(), e.getMessage(), studentId, courseId);
            return false;
        } finally {
            database.closeConnection();
        }

        return false;

    }

    @Override
    public Grade create(Grade baseGrade) {
        var connection = database.openConnection();
        var sql = """
                INSERT INTO grades (grade, type, description, student_id, course_id)\s
                    VALUES (?, ?, ?, ?, ?);
                """;

        try (var statement = connection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            statement.setInt(1, baseGrade.getGrade());
            statement.setString(2, baseGrade.getType().name());
            statement.setString(3, baseGrade.getDescription());
            statement.setInt(4, baseGrade.getStudentId());
            statement.setInt(5, baseGrade.getCourseId());

            statement.execute();

            var resultSet = statement.getGeneratedKeys();

            if (resultSet.next()) {
                var givenGradeId = resultSet.getInt(1);
                baseGrade.setId(givenGradeId);
            } else throw new SQLException("Couldn't create grade");

            resultSet.close();

        }
        catch(SQLException e) {
            throw new RuntimeException(e);
        } finally {
            database.closeConnection();
        }
        return baseGrade;

    }

    @Override
    public List<Grade> findAllByStudentIdAndCourseId(int studentIdQuery, int courseIdQuery) {
        var connection = database.openConnection();
        var sql = "SELECT id, grade, type, description, student_id, course_id " +
                "FROM grades WHERE student_id = ? AND course_id = ?";

        var courseList = new ArrayList<Grade>();

        try (var statement = connection.prepareStatement(sql)) {
            statement.setInt(1, studentIdQuery);
            statement.setInt(2, courseIdQuery);

            var resultSet = statement.executeQuery();

            while (resultSet.next()) {
                var id = resultSet.getInt("id");
                var gradeNumber = resultSet.getInt("grade");
                var type = GradeType.valueOf(resultSet.getString("type"));
                var description = resultSet.getString("description");
                var studentId = resultSet.getInt("student_id");
                var courseId = resultSet.getInt("course_id");
                var grade = new Grade(id, gradeNumber, description, type, studentId, courseId);
                courseList.add(grade);
            }
            resultSet.close();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        database.closeConnection();
        return courseList.stream().toList();

    }

    @Override
    public boolean update(int gradeId, Grade gradeToUpdate) {
        var connection = database.openConnection();
        var sql = """
                UPDATE LOW_PRIORITY grades\s
                SET
                    grade = ?,
                    type = ?,
                    description = ?,
                    student_id = ?,
                    course_id = ?
                WHERE id = ?;
               """;

        try (var statement = connection.prepareStatement(sql)) {
            statement.setInt(1, gradeToUpdate.getGrade());
            statement.setString(2, gradeToUpdate.getType().name());
            statement.setString(3, gradeToUpdate.getDescription());
            statement.setInt(4, gradeToUpdate.getStudentId());
            statement.setInt(5, gradeToUpdate.getCourseId());
            statement.setInt(6, gradeId);

            var affectedRows = statement.executeUpdate();
            statement.close();

            if (affectedRows == 1) return true;

        } catch (SQLException e) {
            System.out.println(e.getMessage());
            throw new RuntimeException(e);
        } finally {
            database.closeConnection();
        }

        return false;

    }
}
