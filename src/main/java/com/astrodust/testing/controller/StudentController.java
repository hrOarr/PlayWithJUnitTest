package com.astrodust.testing.controller;

import com.astrodust.testing.entity.Student;
import com.astrodust.testing.service.StudentService;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "api/v1/students")
@AllArgsConstructor
public class StudentController {

    private static final Logger logger = LoggerFactory.getLogger(StudentController.class);

    @Autowired
    private StudentService studentService;

    @GetMapping
    public List<Student> getAllStudents() {
        return studentService.getAllStudents();
    }

    @PostMapping(value = "/save", consumes = {MediaType.APPLICATION_JSON_VALUE}, produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity addStudent(@RequestBody Student student) {
        Student savedStudent = studentService.addStudent(student);
        logger.info("SoA:: saved!!---- " + savedStudent);
        return ResponseEntity.status(HttpStatus.OK).body(savedStudent);
    }

    @DeleteMapping(value = "/{id}")
    public void deleteStudent(
            @PathVariable("id") Long studentId) {
        studentService.deleteStudent(studentId);
    }
}