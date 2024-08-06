package org.example;

import org.example.controllers.CoursesController;
import org.example.controllers.EnrollmentsController;
import org.example.controllers.GradesController;
import org.example.controllers.StudentsController;
import org.example.entities.Course;
import org.example.entities.Enrollment;
import org.example.entities.Grade;
import org.example.entities.Student;
import org.example.enums.GradeType;
import org.example.models.CoursesModel;
import org.example.models.EnrollmentsModel;
import org.example.models.GradesModel;
import org.example.models.StudentsModel;
import org.example.models.interfaces.ICoursesModel;
import org.example.models.interfaces.IEnrollmentsModel;
import org.example.models.interfaces.IGradesModel;
import org.example.models.interfaces.IStudentsModel;
import org.example.persistence.Database;
import org.example.utils.InputRequester;

import javax.swing.*;
import java.sql.Timestamp;
import java.time.LocalDateTime;
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
        ICoursesModel coursesModel = new CoursesModel(database);
        IGradesModel gradesModel = new GradesModel(database);

        // Controller
        var studentsController = new StudentsController(studentsModel);
        var enrollmentsController = new EnrollmentsController(enrollmentsModel);
        var coursesController = new CoursesController(coursesModel);
        var gradesController = new GradesController(gradesModel);

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
                    7. Crear curso.
                    8. Eliminar curso.
                    9. Listar cursos.
                    10. Inscribir estudiante a un curso.
                    11. Eliminar inscripción de un estudiante a un curso.
                    12. Agregar calificación a un estudiante.
                    13. Editar calificación.
                    14. Listar calificaciones de un estudiante de un curso

                    ************************************************
                    """;
            var option = InputRequester.requestString(menuOptionsMessage);

            switch (option) {
                case "0" -> isMenuOpened = false;
                case "1" -> registerStudent(studentsController);
                case "2" -> editStudent(studentsController);
                case "3" -> listActiveStudents(studentsController);
                case "4" -> showStudentById(studentsController);
                case "5" -> showStudentByEmail(studentsController);
                case "6" -> listStudentEnrollments(studentsController, enrollmentsController, coursesController);
                case "7" -> createCourse(coursesController);
                case "8" -> deleteCourse(coursesController);
                case "9" -> listCourses(coursesController);
                case "10" -> enrollStudentInACourse(enrollmentsController, studentsController, coursesController);
                case "11" -> deleteStudentEnrollment(enrollmentsController, studentsController, coursesController, gradesController);
                case "12" -> addGrade(gradesController, studentsController, enrollmentsController, coursesController);
                case "13" -> editGrade(gradesController, studentsController, enrollmentsController, coursesController);
                case "14" -> listGrades(gradesController, studentsController, enrollmentsController, coursesController);
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

        // Otherwise register student
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

    public static void listStudentEnrollments(
            StudentsController studentsController,
            EnrollmentsController enrollmentsController,
            CoursesController coursesController
    ) {
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
            String noEnrollmentsMessage = String.format("El estudiante '%s' no tiene inscripciones en este momento.", foundStudent.get().getFullName());
            JOptionPane.showMessageDialog(null, noEnrollmentsMessage);
            return;
        }

        var enrollmentsAsListString = getStudentEnrollmentsAsStringList(coursesController, enrollmentList);
        var enrollmentListMessage = String.join("\n--------------\n", enrollmentsAsListString);
        JOptionPane.showMessageDialog(null, enrollmentListMessage);

    }

    public static void createCourse(CoursesController coursesController) {
        var name = InputRequester.requestString("Ingresa el nombre del curso");

        // Check course name availability
        var foundCourse = coursesController.findByName(name);
        if (foundCourse.isPresent()) {
            JOptionPane.showMessageDialog(null, "Ya existe un curso creado con el nombre " + name);
            return;
        }

        // Otherwise create course
        var course = new Course(name);
        var createdCourse = coursesController.create(course);
        JOptionPane.showMessageDialog(null, "¡Curso creado con éxito!\n\n" + createdCourse);

    }

    public static void deleteCourse(CoursesController coursesController) {
        var name = InputRequester.requestString("Ingresa el nombre del curso a eliminar");

        // Find course
        var foundCourse = coursesController.findByName(name);
        if (foundCourse.isEmpty()) {
            JOptionPane.showMessageDialog(null, "No existe ningún curso con el nombre: " + name);
            return;
        }

        boolean couldDeleteCourse = coursesController.delete(foundCourse.get().getId());

        if (couldDeleteCourse) {
            String successMessage = String.format("¡Curso '%s' eliminado con éxito!", foundCourse.get().getName());
            JOptionPane.showMessageDialog(null, successMessage);
        } else
            JOptionPane.showMessageDialog(null, "Error al eliminar el curso.");

    }

    public static void listCourses(CoursesController coursesController) {
        var courseList = coursesController.findAll();

        if (courseList.isEmpty()) {
            JOptionPane.showMessageDialog(null, "No hay cursos creados en el momento.");
            return;
        }

        var coursesAsListString = courseList.stream().map(Course::toString).toList();
        var courseListMessage = String.join("\n--------------\n", coursesAsListString);
        JOptionPane.showMessageDialog(null, courseListMessage);
    }

    public static void enrollStudentInACourse(
            EnrollmentsController enrollmentsController,
            StudentsController studentsController,
            CoursesController coursesController
    ) {
        // Request user to select course
        List<Course> courseList = coursesController.findAll();

        if (courseList.isEmpty()) {
            JOptionPane.showMessageDialog(null, "No hay cursos creados en el momento.");
            return;
        }

        var courseNamesAsListString = courseList.stream().map(Course::getName).toList();
        int courseIndex = InputRequester.requestAnIndexFrom(courseNamesAsListString, "Ingresa el número del curso para hacer la inscripción");
        var course = courseList.get(courseIndex);

        // Request student to enroll
        var studentEmail = InputRequester.requestString("Ingresa el email del estudiante");
        var foundStudent = studentsController.findByEmail(studentEmail);
        if (foundStudent.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Estudiante con email " + studentEmail + " no encontrado.");
            return;
        }

        // Check if user is already register in the course
        int studentId = foundStudent.get().getId();
        var foundEnrollment = enrollmentsController.findByStudentIdAndCourseId(studentId, course.getId());

        if (foundEnrollment.isPresent()) {
            String studentAlreadyEnrolledMessage =
                    String.format("El estudiante '%s' ya está inscrito en el curso '%s' ", foundStudent.get().getFullName(), course.getName());
            JOptionPane.showMessageDialog(null, studentAlreadyEnrolledMessage);
            return;
        }

        // Check if user has less than 3 enrollments
        List<Enrollment> studentEnrollments = enrollmentsController.findAllByStudentId(studentId);

        if (studentEnrollments.size() == 3) {
            String maximumEnrollmentsMessage =
                    String.format("El estudiante '%s' ya está inscrito a 3 cursos. No puede tener más inscripciones", foundStudent.get().getFullName());
            JOptionPane.showMessageDialog(null, maximumEnrollmentsMessage);
            return;
        }

        // Create enrollment
        var enrollment = new Enrollment(Timestamp.valueOf(LocalDateTime.now()), studentId, course.getId());
        enrollmentsController.create(enrollment);
        String studentEnrolledMessage = String.format("¡Estudiante inscrito con éxito al curso '%s'!", course.getName());
        JOptionPane.showMessageDialog(null, studentEnrolledMessage);

    }

    public static void deleteStudentEnrollment(
            EnrollmentsController enrollmentsController,
            StudentsController studentsController,
            CoursesController coursesController,
            GradesController gradesController
    ) {
        // Warn user that all student grades will be deleted
        int wantsToContinueInput = JOptionPane.showConfirmDialog(null, "Está acción también eliminará las notas del usuario en el curso\n" +
                "¿Estás seguro que deseas continuar?", "Eliminanción de notas", JOptionPane.YES_NO_OPTION);

        int YES_OPTION = 0;
        boolean wantsToContinue = wantsToContinueInput == YES_OPTION;
        if (!wantsToContinue) return;

        // Request student
        var studentEmail = InputRequester.requestString("Ingresa el email del estudiante");
        var foundStudent = studentsController.findByEmail(studentEmail);
        if (foundStudent.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Estudiante con email " + studentEmail + " no encontrado.");
            return;
        }
        var studentId = foundStudent.get().getId();


        // Request enrollment to delete
        List<Enrollment> studentEnrollments = enrollmentsController.findAllByStudentId(studentId);
        var enrollmentsAsListString = getStudentEnrollmentsAsStringList(coursesController, studentEnrollments);
        int enrollmentIndexToDelete = InputRequester.requestAnIndexFrom(enrollmentsAsListString, "Introduce el número de la inscripción a eliminar");
        var enrollmentToDelete = studentEnrollments.get(enrollmentIndexToDelete);

        var relatedCourse = coursesController.findById(enrollmentToDelete.getCourseId());

        if (relatedCourse.isEmpty()) throw new RuntimeException("Related enrollment course not found");

        // Delete student grades for the course
        boolean couldDeleteGrades = gradesController.deleteAllByStudentIdAndCourseId(studentId, relatedCourse.get().getId());
        if (couldDeleteGrades) {
            String successfullyDeletedNotesMessage = String.format("¡Notas asociadas al curso '%s' eliminadas con éxito!", relatedCourse.get().getName());
            JOptionPane.showMessageDialog(null, successfullyDeletedNotesMessage);
        } else {
            String errorDeletingNotesMessage = String.format("Error al eliminar notas asociadas al curso '%s'", relatedCourse.get().getName());
            JOptionPane.showMessageDialog(null, errorDeletingNotesMessage);
        }

        // Delete enrollment
        boolean couldDeleteEnrollment = enrollmentsController.delete(enrollmentToDelete.getId());

        if (couldDeleteEnrollment) {
            String successMessage = String.format("¡Inscripción al curso '%s' eliminada con éxito!", relatedCourse.get().getName());
            JOptionPane.showMessageDialog(null, successMessage);
        } else {
            String errorMessage = String.format("Error al eliminar la inscripción al curso '%s'.", relatedCourse.get().getName());
            JOptionPane.showMessageDialog(null, errorMessage);
        }
    }

    public static void addGrade(
            GradesController gradesController,
            StudentsController studentsController,
            EnrollmentsController enrollmentsController,
            CoursesController coursesController
    ) {
        // ************* Request grade data *************
        // Grade
        var gradeNumber = InputRequester.requestInteger("Introduce la nota (entre 0 y 100)");
        if (gradeNumber.isEmpty()) throw new RuntimeException("Grade empty after requesting");
        if (gradeNumber.get() < 0 || gradeNumber.get() > 100) {
            JOptionPane.showMessageDialog(null, "La nota ingresada es inválida", "Nota inválida", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Grade type
        List<String> gradeTypeOptions = GradeType.getGradeTypesAsStringList();
        int gradeTypeIndex = InputRequester.requestAnIndexFrom(gradeTypeOptions, "Ingresa el número del tipo califición");
        var gradeTypeString = gradeTypeOptions.get(gradeTypeIndex);
        var gradeType = GradeType.valueOf(gradeTypeString);

        // Description
        var description = InputRequester.requestString("Introduce la descripción de la nota");
        // **************************


        // Request student
        var studentEmail = InputRequester.requestString("Ingresa el email del estudiante");
        var foundStudent = studentsController.findByEmail(studentEmail);
        if (foundStudent.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Estudiante con email " + studentEmail + " no encontrado.");
            return;
        }
        var studentId = foundStudent.get().getId();

        // Request enrollment to get course id
        List<Enrollment> studentEnrollments = enrollmentsController.findAllByStudentId(studentId);
        var enrollmentsAsListString = getStudentEnrollmentsAsStringList(coursesController, studentEnrollments);
        int enrollmentIndex = InputRequester.requestAnIndexFrom(enrollmentsAsListString, "Introduce el número de la inscripción para seleccionar el curso");
        var enrollment = studentEnrollments.get(enrollmentIndex);

        var grade  = new Grade(gradeNumber.get(), description, gradeType, foundStudent.get().getId(), enrollment.getCourseId());
        gradesController.create(grade);

        JOptionPane.showMessageDialog(null, "¡Nota añadida con éxito!\n\n");
    }

    public static void listGrades(
            GradesController gradesController,
            StudentsController studentsController,
            EnrollmentsController enrollmentsController,
            CoursesController coursesController
    ) {
        // Request student
        var studentEmail = InputRequester.requestString("Ingresa el email del estudiante");
        var foundStudent = studentsController.findByEmail(studentEmail);
        if (foundStudent.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Estudiante con email " + studentEmail + " no encontrado.");
            return;
        }
        var studentId = foundStudent.get().getId();

        // Request enrollment to get course id
        List<Enrollment> studentEnrollments = enrollmentsController.findAllByStudentId(studentId);
        var enrollmentsAsListString = getStudentEnrollmentsAsStringList(coursesController, studentEnrollments);
        int enrollmentIndex = InputRequester.requestAnIndexFrom(enrollmentsAsListString, "Introduce el número de la inscripción para seleccionar el curso");
        var enrollment = studentEnrollments.get(enrollmentIndex);

        // List grades
        List<Grade> gradeList = gradesController.findAllByStudentIdAndCourseId(studentId, enrollment.getCourseId());

        var relatedCourse = coursesController.findById(enrollment.getCourseId());
        if (relatedCourse.isEmpty()) throw new RuntimeException("Related course not found");

        if (gradeList.isEmpty()) {
            String gradesNotFoundMessage = String.format("No hay notas añadidas para el curso '%s'", relatedCourse.get().getName());
            JOptionPane.showMessageDialog(null, gradesNotFoundMessage);
        }

        var gradesAsListString = getGradesAsStringList(coursesController, gradeList);
        var gradeListMessage = String.join("\n--------------\n", gradesAsListString);
        JOptionPane.showMessageDialog(null, gradeListMessage);

    }

    public static void editGrade(
            GradesController gradesController,
            StudentsController studentsController,
            EnrollmentsController enrollmentsController,
            CoursesController coursesController
    ) {
        // Request student
        var studentEmail = InputRequester.requestString("Ingresa el email del estudiante");
        var foundStudent = studentsController.findByEmail(studentEmail);
        if (foundStudent.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Estudiante con email " + studentEmail + " no encontrado.");
            return;
        }
        var studentId = foundStudent.get().getId();

        // Request enrollment to get course id
        List<Enrollment> studentEnrollments = enrollmentsController.findAllByStudentId(studentId);
        var enrollmentsAsListString = getStudentEnrollmentsAsStringList(coursesController, studentEnrollments);
        int enrollmentIndex = InputRequester.requestAnIndexFrom(enrollmentsAsListString, "Introduce el número de la inscripción para seleccionar el curso");
        var enrollment = studentEnrollments.get(enrollmentIndex);

        // Request grade to edit
        List<Grade> gradeList = gradesController.findAllByStudentIdAndCourseId(studentId, enrollment.getCourseId());

        var relatedCourse = coursesController.findById(enrollment.getCourseId());
        if (relatedCourse.isEmpty()) throw new RuntimeException("Related course not found");

        if (gradeList.isEmpty()) {
            String gradesNotFoundMessage = String.format("No hay notas añadidas para el curso '%s'", relatedCourse.get().getName());
            JOptionPane.showMessageDialog(null, gradesNotFoundMessage);
            return;
        }

        var gradesAsListString = getGradesAsStringList(coursesController, gradeList);
        int gradeIndex = InputRequester.requestAnIndexFrom(gradesAsListString, "Introduce el número de la inscripción para seleccionar el curso");
        var grade = gradeList.get(gradeIndex);

        // ************* Request new grade data *************
        // Grade
        var gradeNumberInput = InputRequester.requestInteger("Introduce la nueva nota (entre 0 y 100) (Presiona ENTER para omitar cambio)", true);
        int gradeNumber = gradeNumberInput.orElseGet(grade::getGrade);
        if (gradeNumber < 0 || gradeNumber > 100) {
            JOptionPane.showMessageDialog(null, "La nota ingresada es inválida", "Nota inválida", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Description
        var description = InputRequester.requestString("Introduce la nueva descripción de la nota (Presiona ENTER para omitar cambio)", true);
        // **************************

        grade.setGrade(gradeNumber);
        grade.setDescription(description.isEmpty() ? grade.getDescription() : description);

        boolean couldEditGrade = gradesController.update(foundStudent.get().getId(), grade);
        if (couldEditGrade) {
            String successMessage = "¡Nota editada con éxito!";
            JOptionPane.showMessageDialog(null, successMessage);
        } else
            JOptionPane.showMessageDialog(null, "Error al editar la nota");

    }

    private static List<String> getStudentEnrollmentsAsStringList(CoursesController coursesController, List<Enrollment> studentEnrollments) {
        return studentEnrollments.stream().map(e -> {
            var course = coursesController.findById(e.getCourseId());
            if (course.isEmpty()) throw new RuntimeException("Course with id " + e.getCourseId() + " not found");

            var lines = new String[]{
                    String.format("\s\sID: %d", e.getId()),
                    String.format("\s\sFecha inscripción: %1$Te/%1$Tm/%1$TY %1$Tr", e.getDate()),
                    String.format("\s\sCurso: %s", course.get().getName()),
            };
            return "Inscripción:\n" + String.join("\n", lines);
        }).toList();
    }

    private static List<String> getGradesAsStringList(CoursesController coursesController, List<Grade> gradeList) {
        return gradeList.stream().map(e -> {
            var course = coursesController.findById(e.getCourseId());
            if (course.isEmpty()) throw new RuntimeException("Course with id " + e.getCourseId() + " not found");

            var lines = new String[]{
                    String.format("\s\sID: %d", e.getId()),
                    String.format("\s\sNota: %d", e.getGrade()),
                    String.format("\s\sDescripción: %s", e.getDescription()),
                    String.format("\s\sTipo de calificación: %s", e.getType().name()),
                    String.format("\s\sCurso: %s", course.get().getName()),
            };
            return "Calificación: \n" + String.join("\n", lines);
        }).toList();
    }
}