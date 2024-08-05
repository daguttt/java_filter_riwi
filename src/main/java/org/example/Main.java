package org.example;

import org.example.controllers.EnrollmentsController;
import org.example.controllers.StudentsController;
import org.example.entities.Enrollment;
import org.example.entities.Student;
import org.example.models.EnrollmentsModel;
import org.example.models.StudentsModel;
import org.example.models.interfaces.IEnrollmentsModel;
import org.example.models.interfaces.IStudentsModel;
import org.example.persistence.Database;
import org.example.utils.InputRequester;

import javax.swing.*;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        System.out.println("***********************************************************");
        System.out.println("Template JDBC + MySQL CRUD App");
        System.out.println("***********************************************************");

        String host = args[0];
        String port = args[1];
        String dbName = args[2];
        String dbUser = args[3];
        String dbPassword = args[4];

        // -****************************
        // Dependency Injection
        var database = new Database(host, port, dbName, dbUser, dbPassword);
        database.testConnection();

        // Models
        IStudentsModel studentsModel = new StudentsModel(database);
        IEnrollmentsModel enrollmentsModel = new EnrollmentsModel(database);

        // Controller
        var studentsController = new StudentsController(studentsModel);
        var enrollmentsController = new EnrollmentsController(enrollmentsModel);

        // -****************************

        // Menu
        boolean isMenuOpened = true;
        while (isMenuOpened) {
            String menuOptionsMessage = """
                    ********************* Menu *********************

                    Ingresa la opción que deseas hacer:

                    0. Salir.
                    1. Registrar estudiante. /
                    2. Editar estudiante. /
                    3. Listar estudiantes.
                    4. Buscar estudiante por id.
                    5. Buscar estudiante por correo electrónico.
                    6. Listar inscripciones de un estudiante.

                    ************************************************
                    """;
            var option = InputRequester.requestString(menuOptionsMessage, true);
            boolean wantsToExit = option.isEmpty();
            if (wantsToExit) return;

            switch (option) {
                case "0" -> isMenuOpened = false;
                case "1" -> registerStudent(studentsController);
                case "2" -> editStudent(studentsController);
                case "3" -> listActiveStudents(studentsController);
                case "4" -> showStudentById(studentsController);
                case "5" -> showStudentByEmail(studentsController);
                case "6" -> listStudentEnrollments(studentsController, enrollmentsController);
                default -> JOptionPane.showMessageDialog(null, "Opción inválida. Inténtalo de nuevo");
            }
        }
    }

    public static void registerStudent(StudentsController studentsController) {
        var fullname = InputRequester.requestString("Ingresa el nombre completo del estudiante");
        var email = InputRequester.requestString("Ingresa el email del estudiante");

        // Check if the email is already registered
        var foundStudent = studentsController.findByEmail(email);
        if (foundStudent.isPresent()) {
            JOptionPane.showMessageDialog(null, "Ya existe un estudiante registrado con el correo " + email);
            return;
        }

        // Otherwise register user
        var student = new Student(fullname, email);
        var registeredStudent = studentsController.register(student);
        JOptionPane.showMessageDialog(null, "¡Estudiante registrado con éxito!\n\n" + registeredStudent);

    }

    public static void editStudent(StudentsController studentsController) {
        // Find student to edit
        var studentEmail = InputRequester.requestString("Ingresa el email del estudiante a editar");
        var foundStudent = studentsController.findByEmail(studentEmail);
        if (foundStudent.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Estudiante con email " + studentEmail + " no encontrado.");
            return;
        }

        // Request new values
        String newNamePrompt = String.format("Nombre actual: %s%nIngresa el nuevo nombre completo del estudiante (Presiona ENTER para omitir cambio)",
                foundStudent.get().getFullName());
        String newFullname = InputRequester.requestString(newNamePrompt, true);

        String newEmailPrompt = String.format("Correo actual: %s%nIngresa el nuevo nombre completo del estudiante (Presiona ENTER para omitir cambio)",
                foundStudent.get().getEmail());
        String newEmail = InputRequester.requestString(newEmailPrompt, true);

        var states = List.of("Activo", "Inactivo");
        int newStudentStateIndex = InputRequester.requestAnIndexFrom(states, "Ingresa el número del estado actual del estudiante");
        boolean newStudentState = states.get(newStudentStateIndex).equals("Activo");

        // Edit student in DB
        var studentToEdit = new Student(
                newFullname.isEmpty() ? foundStudent.get().getFullName() : newFullname,
                newEmail.isEmpty() ? foundStudent.get().getEmail() : newEmail,
                newStudentState
        );
        boolean couldEditStudent = studentsController.update(foundStudent.get().getId(), studentToEdit);
        if (couldEditStudent) {
            String successMessage = String.format("¡Estudiante '%s' editado con éxito!", studentToEdit.getFullName());
            JOptionPane.showMessageDialog(null, successMessage);
        } else
            JOptionPane.showMessageDialog(null, "Error al editar el estudiante");
    }

    public static void listActiveStudents(StudentsController studentsController) {
        var studentList = studentsController.findAll();

        if (studentList.isEmpty()) {
            JOptionPane.showMessageDialog(null, "No hay estudiantes activos en el momento.");
            return;
        }

        var studentsAsListString = studentList.stream().map(Student::toString).toList();
        var studentListMessage = String.join("\n--------------\n", studentsAsListString);
        JOptionPane.showMessageDialog(null, studentListMessage);
    }

    public static void showStudentById(StudentsController studentsController) {
        var studentId = InputRequester.requestInteger("Ingresa el ID del estudiante que quieres buscar");

        if (studentId.isEmpty()) throw new RuntimeException("Student id empty after requesting");

        var foundStudent = studentsController.findById(studentId.get());
        if (foundStudent.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Estudiante con email " + studentId + " no encontrado.");
            return;
        }

        String successMessage = String.format("Se encontró un estudiante con el ID '%d'%n%n%s", studentId.get(), foundStudent.get());
        JOptionPane.showMessageDialog(null, successMessage);

    }

    public static void showStudentByEmail(StudentsController studentsController) {
        var studentEmail = InputRequester.requestString("Ingresa el email del estudiante que quieres buscar");

        var foundStudent = studentsController.findByEmail(studentEmail);
        if (foundStudent.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Estudiante con email " + studentEmail + " no encontrado.");
            return;
        }

        String successMessage = String.format("Se encontró un estudiante con el email '%s'%n%n%s", studentEmail, foundStudent.get());
        JOptionPane.showMessageDialog(null, successMessage);
    }

    public static void listStudentEnrollments(StudentsController studentsController, EnrollmentsController enrollmentsController) {
        // Find student to show enrollments
        var studentEmail = InputRequester.requestString("Ingresa el email del estudiante");
        var foundStudent = studentsController.findByEmail(studentEmail);
        if (foundStudent.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Estudiante con email " + studentEmail + " no encontrado.");
            return;
        }

        // Find student enrollments
        var enrollmentList = enrollmentsController.findAllByStudentId(foundStudent.get().getId());
        if (enrollmentList.isEmpty()) {
            JOptionPane.showMessageDialog(null, "No hay estudiantes activos en el momento.");
            return;
        }

        var enrollmentsAsListString = enrollmentList.stream().map(Enrollment::toString).toList();
        var enrollmentListMessage = String.join("\n--------------\n", enrollmentsAsListString);
        JOptionPane.showMessageDialog(null, enrollmentListMessage);

    }

}