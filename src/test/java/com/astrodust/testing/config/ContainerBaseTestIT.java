package com.astrodust.testing.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class ContainerBaseTestIT {

    @Autowired
    protected MockMvc mockMvc;

    @Container
    static final MySQLContainer MY_SQL_CONTAINER =
            new MySQLContainer("mysql:latest").withDatabaseName("demo").withUsername("astrodust").withPassword("qwerty@#!");

//    static {
//        MY_SQL_CONTAINER = new MySQLContainer("mysql:latest").withDatabaseName("demo").withUsername("astrodust").withPassword("qwerty@#!");
//        MY_SQL_CONTAINER.start();
//    }

    @DynamicPropertySource
    static void mySQLConfig(DynamicPropertyRegistry registry){
        registry.add("spring.datasource.url", MY_SQL_CONTAINER::getJdbcUrl);
        registry.add("spring.datasource.username", MY_SQL_CONTAINER::getUsername);
        registry.add("spring.datasource.password", MY_SQL_CONTAINER::getPassword);
    }
}
