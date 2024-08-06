package org.example.models;

import org.example.entities.Course;
import org.example.models.interfaces.ICoursesModel;
import org.example.persistence.Database;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CoursesModel implements ICoursesModel {
    private final Database database;

    public CoursesModel(Database database) {
        this.database = database;
    }

    @Override
    public Course create(Course baseCourse) {
        var connection = database.openConnection();
        var sql = """
                INSERT INTO courses (name)\s
                    VALUES (?);
                """;

        try (var statement = connection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, baseCourse.getName());

            statement.execute();

            var resultSet = statement.getGeneratedKeys();

            if (resultSet.next()) {
                var givenCourseId = resultSet.getInt(1);
                baseCourse.setId(givenCourseId);
            } else throw new SQLException("Couldn't create course");

            resultSet.close();

        }
        catch(SQLException e) {
            throw new RuntimeException(e);
        } finally {
            database.closeConnection();
        }
        return baseCourse;

    }

    @Override
    public Optional<Course> findByName(String nameQuery) {
        var connection = database.openConnection();
        var sql = """
                SELECT id, name\s
                FROM courses WHERE name = ?
                """;

        Optional<Course> course = Optional.empty();
        try(var statement = connection.prepareStatement(sql)) {
            statement.setString(1, nameQuery);

            var resultSet = statement.executeQuery();
            if (resultSet.next()) {
                var id = resultSet.getInt("id");
                var name = resultSet.getString("name");
                course = Optional.of(new Course(id, name));
            }
            resultSet.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        database.closeConnection();
        return course;

    }

    @Override
    public boolean delete(int courseId) {
        var connection = database.openConnection();
        var sql = """
                DELETE FROM courses\s
                WHERE id = ?;
               """;

        try (var statement = connection.prepareStatement(sql)) {
            statement.setInt(1, courseId);

            var affectedRows = statement.executeUpdate();
            statement.close();

            if (affectedRows == 1) return true;

        } catch (SQLException e) {
            System.out.printf("Error deleting course (Error %s): %s%ncourseId: %d", e.getClass(), e.getMessage(), courseId);
            return false;
        } finally {
            database.closeConnection();
        }

        return false;

    }

    @Override
    public List<Course> findAll() {
        var connection = database.openConnection();
        var sql = "SELECT id, name FROM courses";

        var courseList = new ArrayList<Course>();

        try (var statement = connection.createStatement()) {

            var resultSet = statement.executeQuery(sql);

            while (resultSet.next()) {
                var id = resultSet.getInt("id");
                var name = resultSet.getString("name");
                var course = new Course(id, name);
                courseList.add(course);
            }
            resultSet.close();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        database.closeConnection();
        return courseList.stream().toList();

    }

    @Override
    public Optional<Course> findById(int courseIdQuery) {
        var connection = database.openConnection();
        var sql = """
                SELECT id, name\s
                FROM courses WHERE id = ?
                """;

        Optional<Course> course = Optional.empty();
        try(var statement = connection.prepareStatement(sql)) {
            statement.setInt(1, courseIdQuery);

            var resultSet = statement.executeQuery();
            if (resultSet.next()) {
                var id = resultSet.getInt("id");
                var name = resultSet.getString("name");
                course = Optional.of(new Course(id, name));
            }
            resultSet.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        database.closeConnection();
        return course;

    }
}
