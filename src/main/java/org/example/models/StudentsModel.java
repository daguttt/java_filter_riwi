package org.example.models;

import org.example.entities.Student;
import org.example.models.interfaces.IStudentsModel;
import org.example.persistence.Database;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class StudentsModel implements IStudentsModel {
    private final Database database;

    public StudentsModel(Database database) {
        this.database = database;
    }
    @Override
    public Student register(Student baseStudent) {
        var connection = database.openConnection();
        var sql = """
                INSERT INTO students (fullname, email, is_active)\s
                    VALUES (?, ?, ?);
                """;

        try (var statement = connection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, baseStudent.getFullName());
            statement.setString(2, baseStudent.getEmail());
            statement.setBoolean(3, baseStudent.isActive());

            statement.execute();

            var resultSet = statement.getGeneratedKeys();

            if (resultSet.next()) {
                var givenStudentId = resultSet.getInt(1);
                baseStudent.setId(givenStudentId);
            } else throw new SQLException("Couldn't register student");

            resultSet.close();

        }
        catch(SQLException e) {
            throw new RuntimeException(e);
        } finally {
            database.closeConnection();
        }
        return baseStudent;
    }

    @Override
    public boolean update(int studentId, Student studentToUpdate) {
        var connection = database.openConnection();
        var sql = """
                UPDATE LOW_PRIORITY students\s
                SET
                    fullname = ?,
                    email = ?,
                    is_active = ?,
                WHERE id = ?;
               """;

        try (var statement = connection.prepareStatement(sql)) {
            statement.setString(1, studentToUpdate.getFullName());
            statement.setString(2, studentToUpdate.getEmail());
            statement.setBoolean(3, studentToUpdate.isActive());
            statement.setInt(4, studentId);

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

    @Override
    public List<Student> findAllActive() {
        var connection = database.openConnection();
        var sql = """
                SELECT id, fullname, email, is_active\s
                FROM students WHERE is_active = true;
                """;

        var studentList = new ArrayList<Student>();

        try (var statement = connection.createStatement()) {

            var resultSet = statement.executeQuery(sql);

            while (resultSet.next()) {
                var id = resultSet.getInt("id");
                var name = resultSet.getString("fullname");
                var email = resultSet.getString("email");
                var isActive = resultSet.getBoolean("is_active");
                var student = new Student(id, name, email, isActive);
                studentList.add(student);
            }
            resultSet.close();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        database.closeConnection();
        return studentList.stream().toList();

    }

    @Override
    public Optional<Student> findById(int studentId) {
        var connection = database.openConnection();
        var sql = """
                SELECT id, fullname, email, is_active\s
                FROM students WHERE id = ?
                """;

        Optional<Student> student = Optional.empty();
        try(var statement = connection.prepareStatement(sql)) {
            statement.setInt(1, studentId);

            var resultSet = statement.executeQuery();
            if (resultSet.next()) {
                var id = resultSet.getInt("id");
                var fullname = resultSet.getString("fullname");
                var email = resultSet.getString("email");
                var isActive = resultSet.getBoolean("is_active");
                student = Optional.of(new Student(id, fullname, email, isActive));
            }
            resultSet.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        database.closeConnection();
        return student;
    }

    @Override
    public Optional<Student> findByEmail(String studentEmail) {
        var connection = database.openConnection();
        var sql = """
                SELECT id, fullname, email, is_active\s
                FROM students WHERE email = ?
                """;

        Optional<Student> student = Optional.empty();
        try(var statement = connection.prepareStatement(sql)) {
            statement.setString(1, studentEmail);

            var resultSet = statement.executeQuery();
            if (resultSet.next()) {
                var id = resultSet.getInt("id");
                var name = resultSet.getString("fullname");
                var email = resultSet.getString("email");
                var isActive = resultSet.getBoolean("is_active");
                student = Optional.of(new Student(id, name, email, isActive));
            }
            resultSet.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        database.closeConnection();
        return student;
    }
}
