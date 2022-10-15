package com.astrodust.testing.unit.service;

import com.astrodust.testing.entity.Student;
import com.astrodust.testing.enums.Gender;
import com.astrodust.testing.exceptions.BadRequestException;
import com.astrodust.testing.exceptions.ResourceNotFoundException;
import com.astrodust.testing.repository.StudentRepository;
import com.astrodust.testing.service.StudentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.TestPropertySource;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@TestPropertySource(locations = {"classpath:application.properties"})
class StudentServiceTest {

    @InjectMocks
    private StudentService underTest;

    @Mock
    private StudentRepository studentRepository;

    @BeforeEach
    void setUp(){
        //AutoCloseable autoCloseable = MockitoAnnotations.openMocks(this);
        //underTest = new StudentService(studentRepository);
    }

    @Test
    void getAllStudents() {
        // when
        underTest.getAllStudents();

        // then
        verify(studentRepository).findAll();
    }

    @Test
    void addStudent() {
        // given
        String email = "tareq@gmail.com";
        Student student = new Student(
                "Tareq",
                email,
                Gender.MALE
        );

        when(studentRepository.selectExistsEmail(anyString())).thenReturn(false);
        when(studentRepository.save(any(Student.class))).thenAnswer(ans->ans.getArguments()[0]);

        // when
        Student savedStudent = underTest.addStudent(student);

        // then
        ArgumentCaptor<Student> studentArgumentCaptor = ArgumentCaptor.forClass(Student.class);
        verify(studentRepository).save(studentArgumentCaptor.capture());

        Student capturedStudent = studentArgumentCaptor.getValue();
        assertThat(capturedStudent).isEqualTo(student);
        assertThat(savedStudent).isEqualTo(student);
    }

    @Test
    void checkExceptionWhenEmailAlreadyExists() {
        // given
        String email = "tareq@gmail.com";
        Student student = new Student(
                "Tareq",
                email,
                Gender.MALE
        );
        given(studentRepository.selectExistsEmail(anyString())).willReturn(true);

        // when

        // then
        assertThatThrownBy(()->underTest.addStudent(student))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("Email " + student.getEmail() + " taken");

        verify(studentRepository, never()).save(any());
    }

    @ParameterizedTest
    @CsvSource({
            "10",
            "-1",
            "0"
    })
    void deleteStudent(Long studentId) {
        // given
        given(studentRepository.existsById(anyLong())).willReturn(true);

        // when
        underTest.deleteStudent(studentId);

        // then
        ArgumentCaptor<Long> argumentCaptor = ArgumentCaptor.forClass(Long.class);
        verify(studentRepository).deleteById(argumentCaptor.capture());

        Long actual = argumentCaptor.getValue();
        assertThat(actual).isEqualTo(studentId);
    }

    @ParameterizedTest
    @CsvSource({
            "10",
            "-1",
            "0"
    })
    void checkExceptionWhenDeletingStudent(Long studentId) {
        // given
        given(studentRepository.existsById(anyLong())).willReturn(false);

        // when

        // then
        assertThatThrownBy(()->underTest.deleteStudent(studentId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Student with id " + studentId + " does not exists");

        verify(studentRepository, never()).deleteById(anyLong());
    }
}