package com.astrodust.testing.unit.controller;

import com.astrodust.testing.controller.StudentController;
import com.astrodust.testing.entity.Student;
import com.astrodust.testing.enums.Gender;
import com.astrodust.testing.service.StudentService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest(StudentController.class)
class StudentControllerTest {

    private static final String URI = "/api/v1/students";

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private StudentService studentService;

    @Test
    void getAllStudents() throws Exception {
        // given
        Student student1 = new Student(1L,"Student-1", "student1@gmail.com", Gender.MALE);
        Student student2 = new Student(2L,"Student-2", "student2@gmail.com", Gender.MALE);
        List<Student> studentList = new ArrayList<>();
        studentList.add(student1); studentList.add(student2);
        given(studentService.getAllStudents()).willReturn(studentList);

        // when + then
        mockMvc.perform(get(URI))
                .andExpect(status().isOk()).andExpect(content().json(new ObjectMapper().writeValueAsString(studentList)));
    }

    @ParameterizedTest
    @CsvSource({
            "0,Student-1, student1@gmail.com, MALE",
            "0,Student-3, , MALE",
    })
    void addStudent(Long id, String name, String email, Gender gender) throws Exception {
        // given
        Student student1 = new Student(id, name, email, gender);

        // when + then
        mockMvc.perform(post(URI+"/save").content(new ObjectMapper().writeValueAsString(student1)).contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk());

        ArgumentCaptor<Student> argumentCaptor = ArgumentCaptor.forClass(Student.class);
        verify(studentService).addStudent(argumentCaptor.capture());

        Student actual = argumentCaptor.getValue();
        assertThat(actual).isEqualTo(student1);
    }

    @ParameterizedTest
    @CsvSource({
            "10",
            "-1",
            "0"
    })
    void deleteStudent(Long studentId) throws Exception {
        // given

        // when + then
        mockMvc.perform(delete(URI+"/{id}", studentId))
                .andExpect(status().isOk());

        ArgumentCaptor<Long> argumentCaptor = ArgumentCaptor.forClass(Long.class);
        verify(studentService).deleteStudent(argumentCaptor.capture());

        Long actual = argumentCaptor.getValue();
        assertThat(actual).isEqualTo(studentId);
    }
}