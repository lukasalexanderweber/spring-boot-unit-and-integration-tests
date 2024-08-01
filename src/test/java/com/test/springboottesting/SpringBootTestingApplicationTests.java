package com.test.springboottesting;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.test.springboottesting.employee.EmployeeController;
import com.test.springboottesting.employee.EmployeeRepository;
import com.test.springboottesting.employee.EmployeeService;

/**
 * You can test on an upper level that the test context is loaded correctly
 */
@SpringBootTest
class SpringBootTestingApplicationTests {

  @Autowired
  private EmployeeController employeeController;

  @Autowired
  private EmployeeRepository employeeRepository;

  @Autowired
  private EmployeeService employeeService;

  @Autowired
  private ObjectMapper objectMapper;


  @Test
  void contextLoads() {
    assertThat(employeeController).isNotNull();
    assertThat(employeeRepository).isNotNull();
    assertThat(employeeService).isNotNull();
    assertThat(objectMapper).isNotNull();
  }
}
