package com.test.springboottesting.employee;

import static org.hamcrest.CoreMatchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * An integration test to test request against our web api (controller)
 * 
 * With this test, we can proof that we can fulfill our requirements in a real world scenario with a
 * productive Postgres Database, which is for this test hosted via Testcontainers.
 * 
 * If you look at the Controller Unit Test {@link EmployeeControllerTest} you can see that the
 * "when" (making the request) and "then" (validating the response) parts are equal. We don't want
 * this code duplication. In {@link EmployeeControllerH2IT} I show how we could reuse requests
 * ({@link EmployeeControllerRequests}) and assertions ({@link EmployeeControllerAssertions})
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(replace = Replace.NONE)
class EmployeeControllerPostgresIT extends AbstractPostgresIT {
  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private EmployeeRepository employeeRepository;

  @Autowired
  private ObjectMapper objectMapper;

  private Employee employee;

  @BeforeEach
  void setup() {
    employeeRepository.deleteAll();
    employee =
        Employee.builder().firstName("John").lastName("Doe").email("johndoe@email.com").build();
  }

  @Test
  void givenEmployeeObject_whenCreateEmployee_thenReturnSavedEmployee() throws Exception {
    ResultActions response =
        mockMvc.perform(post("/api/employees").contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(employee)));

    response.andDo(print()).andExpect(status().isCreated())
        .andExpect(jsonPath("$.firstName", is(employee.getFirstName())))
        .andExpect(jsonPath("$.lastName", is(employee.getLastName())))
        .andExpect(jsonPath("$.email", is(employee.getEmail())));
  }

  @Test
  void givenEmailAlreadyExist_whenCreateEmployee_then400() throws Exception {
    employeeRepository.save(employee);

    ResultActions response =
        mockMvc.perform(post("/api/employees").contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(employee)));

    response.andDo(print()).andExpect(status().isBadRequest())
        .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
        .andExpect(jsonPath("$.status", is(HttpStatus.BAD_REQUEST.value()))).andExpect(
            jsonPath("$.detail", is("Email \"" + employee.getEmail() + "\" already exists")));
  }

  @Test
  void givenInvalidEmailFormat_whenCreateEmployee_then400() throws Exception {
    employee.setEmail("invalid@comma,com");

    ResultActions response =
        mockMvc.perform(post("/api/employees").contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(employee)));

    response.andDo(print()).andExpect(status().isBadRequest())
        .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
        .andExpect(jsonPath("$.status", is(HttpStatus.BAD_REQUEST.value()))).andExpect(jsonPath(
            "$.detail", is("Invalid Field(s): {email=must be a well-formed email address}")));
  }

  @Test
  void givenListOfEmployees_whenGetAllEmployees_thenReturnEmployeesList() throws Exception {
    List<Employee> listOfEmployees = new ArrayList<>();
    listOfEmployees.add(employee);
    listOfEmployees.add(
        Employee.builder().firstName("Jane").lastName("Roe").email("janeroe@email.com").build());
    employeeRepository.saveAll(listOfEmployees);

    ResultActions response = mockMvc.perform(get("/api/employees"));

    response.andDo(print()).andExpect(status().isOk())
        .andExpect(jsonPath("$.size()", is(listOfEmployees.size())));
  }

  @Test
  void givenExistingEmployee_whenGetEmployeeById_thenReturnEmployee() throws Exception {
    Employee savedEmployee = employeeRepository.save(employee);

    ResultActions response = mockMvc.perform(get("/api/employees/{id}", savedEmployee.getId()));

    response.andDo(print()).andExpect(status().isOk())
        .andExpect(jsonPath("$.firstName", is(employee.getFirstName())));
  }

  @Test
  void givenNotExistingEmployee_whenGetEmployeeById_thenNotFound() throws Exception {
    ResultActions response = mockMvc.perform(get("/api/employees/1"));

    response.andDo(print()).andExpect(status().isNotFound());
  }

  @Test
  void givenUpdatedEmployee_whenPutEmployee_thenReturnUpdatedEmployee() throws Exception {
    Employee savedEmployee = employeeRepository.save(employee);
    Employee updatedEmployee = Employee.builder().id(savedEmployee.getId()).firstName("Jane")
        .lastName("Roe").email("janeroe@email.com").build();

    ResultActions response =
        mockMvc.perform(put("/api/employees").contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(updatedEmployee)));

    response.andDo(print()).andExpect(status().isOk())
        .andExpect(jsonPath("$.firstName", is(updatedEmployee.getFirstName())))
        .andExpect(jsonPath("$.lastName", is(updatedEmployee.getLastName())))
        .andExpect(jsonPath("$.email", is(updatedEmployee.getEmail())));
  }

  @Test
  void givenEmployeeId_whenDeleteEmployee_thenReturn200() throws Exception {
    Employee savedEmployee = employeeRepository.save(employee);

    ResultActions response = mockMvc.perform(delete("/api/employees/{id}", savedEmployee.getId()));

    response.andExpect(status().isOk()).andDo(print());
  }

}
