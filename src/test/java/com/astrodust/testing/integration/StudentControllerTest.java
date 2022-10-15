package com.astrodust.testing.integration;

import com.astrodust.testing.config.ContainerBaseTestIT;
import com.astrodust.testing.entity.Student;
import com.astrodust.testing.enums.Gender;
import com.astrodust.testing.repository.StudentRepository;
import com.astrodust.testing.service.StudentService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class StudentControllerTest extends ContainerBaseTestIT {

    private static final String URI = "/api/v1/students";

    @Autowired
    private StudentService studentService;

    @Autowired
    private StudentRepository studentRepository;

    @BeforeEach
    void setUp(){
        studentRepository.deleteAll();
    }

    @Test
    void givenListOfStudents_WhenGetAllStudents() throws Exception {
        // given
        Student student1 = new Student(0L,"Student-1", "student1@gmail.com", Gender.MALE);
        Student student2 = new Student(0L,"Student-2", "student2@gmail.com", Gender.MALE);
        List<Student> studentList = new ArrayList<>();
        studentList.add(student1); studentList.add(student2);
        studentService.addStudent(student1);
        studentService.addStudent(student2);

        // when
        ResultActions response = mockMvc.perform(get(URI));

        // then
        response.andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.size()", is(studentList.size())))
                .andExpect(jsonPath("$[0].name", is(student1.getName())))
                .andExpect(jsonPath("$[1].name", is(student2.getName())));
    }


    @ParameterizedTest
    @CsvSource({
            "0,Student-1, student1@gmail.com, MALE",
            "0,Student-2, student2@gmail.com, MALE",
    })
    void addStudent(Long id, String name, String email, Gender gender) throws Exception {
        // given
        Student student = new Student(id, name, email, gender);

        // when
        ResultActions response = mockMvc.perform(post(URI+"/save").content(new ObjectMapper().writeValueAsString(student)).contentType(MediaType.APPLICATION_JSON_VALUE));

        // then
        response.andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.name", is(name)))
                .andExpect(jsonPath("$.email", is(email)));
    }

    @ParameterizedTest
    @CsvSource({
            "0,Student-1, student1@gmail.com, MALE"
    })
    void addStudent_WhenGetEmailExistsException(Long id, String name, String email, Gender gender) throws Exception {
        // given
        Student student = new Student(id, name, email, gender);
        studentService.addStudent(student);

        // when
        ResultActions response = mockMvc.perform(post(URI+"/save").content(new ObjectMapper().writeValueAsString(student)).contentType(MediaType.APPLICATION_JSON_VALUE));

        // then
        response.andExpect(status().isBadRequest())
                .andDo(print());
    }

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    class NestedStudentControllerTest{

        @BeforeAll
        void nestedSetup(){

        }

        @Test
        void deleteStudent() throws Exception {
            // given
            Student student1 = new Student(0L,"Student-1", "student1@gmail.com", Gender.MALE);
            List<Student> studentList = new ArrayList<>();
            studentList.add(student1);
            Student savedOne = studentService.addStudent(student1);

            // when
            ResultActions response = mockMvc.perform(delete(URI+"/{id}", savedOne.getId()));

            // then
            response.andExpect(status().isOk())
                    .andDo(print());
        }
    }
}