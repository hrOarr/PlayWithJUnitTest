package com.astrodust.testing.unit.repository;

import com.astrodust.testing.entity.Student;
import com.astrodust.testing.enums.Gender;
import com.astrodust.testing.repository.StudentRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DataJpaTest
class StudentRepositoryTest {

    @Autowired
    private StudentRepository studentRepository;

    @Test
    void checkIfStudentExistsByEmail() {
        // given
        String email = "tareq@gmail.com";
        Student student = new Student(
                "Tareq",
                email,
                Gender.MALE
        );
        studentRepository.save(student);

        // when
        boolean actual = studentRepository.selectExistsEmail(email);

        // then
        boolean expected = true;
        assertThat(actual).isTrue();
    }

    @Test
    void checkIfStudentDoesNotExistsByEmail() {
        // given
        String email = "tareq@gmail.com";

        // when
        boolean actual = studentRepository.selectExistsEmail(email);

        // then
        boolean expected = false;
        assertThat(actual).isFalse();
    }
}