DROP DATABASE IF EXISTS riwi_academy_db;

CREATE DATABASE riwi_academy_db;

USE riwi_academy_db;

CREATE TABLE students(
	id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(150) NOT NULL,
    email VARCHAR(150) NOT NULL UNIQUE,
    is_active BOOLEAN DEFAULT true
);

CREATE TABLE courses(
	id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(255) NOT NULL
);

CREATE TABLE enrollments(
	id INT PRIMARY KEY AUTO_INCREMENT,
    date DATETIME DEFAULT CURRENT_TIMESTAMP,
    student_id INT NOT NULL,
    course_id INT NOT NULL,
    CONSTRAINT fk_enrollment_student_id FOREIGN KEY (student_id) REFERENCES students(id),
    CONSTRAINT fk_enrollment_course_id FOREIGN KEY (course_id) REFERENCES courses(id)
);

CREATE TABLE grades(
	id INT PRIMARY KEY AUTO_INCREMENT,
    grade INT NOT NULL,
    type ENUM('QUIZ', 'WORKSHOP', 'ASSESSMENT') NOT NULL,
    description VARCHAR(255) NOT NULL,
    student_id INT NOT NULL,
    course_id INT NOT NULL,
    CONSTRAINT grade_chk_gt_zero_lte_hundred CHECK(grade > 0 AND grade <= 100),
    CONSTRAINT fk_grade_student_id FOREIGN KEY (student_id) REFERENCES students(id),
    CONSTRAINT fk_grade_course_id FOREIGN KEY (course_id) REFERENCES courses(id)
);