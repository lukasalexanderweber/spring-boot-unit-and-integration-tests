package com.test.springboottesting.employee;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

/**
 * This is just to demonstrate how mockito could be set up without MockitoExtension dependency
 * injection. May be useful for projects not related to spring boot
 */
class EmployeeServiceWithoutMockitoExtensionTest {

  private EmployeeRepository employeeRepository;
  private EmployeeService employeeService;
  private Employee employee;

  @BeforeEach
  void setup() {
    employeeRepository = Mockito.mock(EmployeeRepository.class);
    employeeService = new EmployeeService(employeeRepository);
    employee =
        Employee.builder().id(1).firstName("John").lastName("Doe").email("johndoe@email.com").build();
  }

  @Test
  void test() {
    given(employeeRepository.findByEmail(employee.getEmail())).willReturn(Optional.empty());
    given(employeeRepository.save(employee)).willReturn(employee);

    Employee savedEmployee = employeeService.saveEmployee(employee);

    assertThat(savedEmployee.getId()).isPositive();
  }

}
