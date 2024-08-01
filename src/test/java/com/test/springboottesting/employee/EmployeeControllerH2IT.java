package com.test.springboottesting.employee;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

/**
 * This test runs the same integration test as {@link EmployeeControllerPostgresIT}, but with an in
 * memory h2 database (default). We can see that with the outsourced request and assert
 * functionality the test are clean and precise. Requests and Asserts can be reused in unit tests as
 * well.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(TestConfig.class) // we need this to be able to autowire EmployeeControllerRequests
@AutoConfigureMockMvc
class EmployeeControllerH2IT {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private EmployeeRepository employeeRepository;

  @Autowired
  private EmployeeControllerRequests requests;

  private Employee employee;

  @BeforeEach
  void setup() {
    employeeRepository.deleteAll();
    employee =
        Employee.builder().firstName("John").lastName("Doe").email("johndoe@email.com").build();
  }

  @Test
  void givenEmployeeObject_whenCreateEmployee_thenReturnSavedEmployee() throws Exception {
    ResultActions response = requests.postEmployee(employee);

    EmployeeControllerAssertions.assertThat(response).hasStatus(HttpStatus.CREATED)
        .hasEmployeeInBody(employee);
    assertThat(employeeRepository.count()).isEqualTo(1);
  }

  @Test
  void givenEmailAlreadyExist_whenCreateEmployee_then400() throws Exception {
    employeeRepository.save(employee);

    ResultActions response = requests.postEmployee(employee);

    EmployeeControllerAssertions.assertThat(response).hasStatus(HttpStatus.BAD_REQUEST)
        .hasMediaType(MediaType.APPLICATION_PROBLEM_JSON)
        .hasProblemJsonStatus(HttpStatus.BAD_REQUEST)
        .hasProblemJsonDetail("Email \"" + employee.getEmail() + "\" already exists");
    assertThat(employeeRepository.count()).isEqualTo(1);
  }

  @Test
  void givenInvalidEmailFormat_whenCreateEmployee_then400() throws Exception {
    employee.setEmail("invalid@comma,com");

    ResultActions response = requests.postEmployee(employee);

    EmployeeControllerAssertions.assertThat(response).hasStatus(HttpStatus.BAD_REQUEST)
        .hasMediaType(MediaType.APPLICATION_PROBLEM_JSON)
        .hasProblemJsonStatus(HttpStatus.BAD_REQUEST)
        .hasProblemJsonDetail("Invalid Field(s): {email=must be a well-formed email address}");;
    assertThat(employeeRepository.count()).isZero();
  }


  @Test
  void givenListOfEmployees_whenGetAllEmployees_thenReturnEmployeesList() throws Exception {
    List<Employee> listOfEmployees = new ArrayList<>();
    listOfEmployees.add(employee);
    listOfEmployees.add(
        Employee.builder().firstName("Jane").lastName("Roe").email("janeroe@email.com").build());
    employeeRepository.saveAll(listOfEmployees);

    ResultActions response = requests.getEmployees();

    EmployeeControllerAssertions.assertThat(response).hasStatus(HttpStatus.OK)
        .hasSize(listOfEmployees.size());
  }

  @Test
  void givenExistingEmployee_whenGetEmployeeById_thenReturnEmployee() throws Exception {
    Employee savedEmployee = employeeRepository.save(employee);

    ResultActions response = requests.getEmployee(savedEmployee.getId());

    EmployeeControllerAssertions.assertThat(response).hasStatus(HttpStatus.OK)
        .hasEmployeeInBody(savedEmployee);
  }

  @Test
  void givenNotExistingEmployee_whenGetEmployeeById_thenNotFound() throws Exception {
    ResultActions response = mockMvc.perform(get("/api/employees/1"));

    EmployeeControllerAssertions.assertThat(response).hasStatus(HttpStatus.NOT_FOUND);
  }

  @Test
  void givenUpdatedEmployee_whenPutEmployee_thenReturnUpdatedEmployee() throws Exception {
    Employee savedEmployee = employeeRepository.save(employee);
    Employee updatedEmployee = Employee.builder().id(savedEmployee.getId()).firstName("Jane")
        .lastName("Roe").email("janeroe@email.com").build();

    ResultActions response = requests.putEmployee(updatedEmployee);

    EmployeeControllerAssertions.assertThat(response).hasStatus(HttpStatus.OK)
        .hasEmployeeInBody(updatedEmployee);
  }

  @Test
  void givenEmployeeId_whenDeleteEmployee_thenReturn200() throws Exception {
    Employee savedEmployee = employeeRepository.save(employee);

    ResultActions response = mockMvc.perform(delete("/api/employees/{id}", savedEmployee.getId()));

    EmployeeControllerAssertions.assertThat(response).hasStatus(HttpStatus.OK);
    assertThat(employeeRepository.count()).isZero();
  }
}
