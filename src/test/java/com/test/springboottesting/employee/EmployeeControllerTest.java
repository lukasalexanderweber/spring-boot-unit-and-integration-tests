package com.test.springboottesting.employee;

import static org.hamcrest.CoreMatchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
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
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.test.springboottesting.exception.DuplicateEmailException;

/**
 * This is a unit test where with {@link MockMvc} requests are send to our
 * {@link EmployeeController}. {@link EmployeeService} is mocked. See that there is a lot of code
 * duplication with {@link EmployeeControllerPostgresIT}. In {@link EmployeeControllerH2IT} an
 * approach which is more readable and with less code duplication is presented.
 */
@WebMvcTest
class EmployeeControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockBean // mock and register bean in the application context
  private EmployeeService employeeService;

  @Autowired
  private ObjectMapper objectMapper;

  private Employee employee;

  @BeforeEach
  void setup() {
    employee =
        Employee.builder().id(1).firstName("John").lastName("Doe").email("johndoe@email.com").build();
  }

  @Test
  void givenEmployeeObject_whenCreateEmployee_thenReturnSavedEmployee() throws Exception {
    given(employeeService.saveEmployee(any(Employee.class)))
        .willAnswer(invocation -> invocation.getArgument(0)); // return the argument given to it

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
    given(employeeService.saveEmployee(any(Employee.class)))
        .willThrow(new DuplicateEmailException(employee.getEmail()));

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
    given(employeeService.saveEmployee(any(Employee.class)))
        .willAnswer(invocation -> invocation.getArgument(0));

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
    given(employeeService.getAllEmployees()).willReturn(listOfEmployees);

    ResultActions response = mockMvc.perform(get("/api/employees"));

    response.andDo(print()).andExpect(status().isOk())
        .andExpect(jsonPath("$.size()", is(listOfEmployees.size())));
  }

  @Test
  void givenExistingEmployee_whenGetEmployeeById_thenReturnEmployee() throws Exception {
    given(employeeService.getEmployeeById(employee.getId())).willReturn(Optional.of(employee));

    ResultActions response = mockMvc.perform(get("/api/employees/1"));

    response.andDo(print()).andExpect(status().isOk())
        .andExpect(jsonPath("$.firstName", is(employee.getFirstName())));
  }

  @Test
  void givenNotExistingEmployee_whenGetEmployeeById_thenNotFound() throws Exception {
    given(employeeService.getEmployeeById(any(Long.class))).willReturn(Optional.empty());

    ResultActions response = mockMvc.perform(get("/api/employees/1"));

    response.andDo(print()).andExpect(status().isNotFound());
  }

  @Test
  void givenUpdatedEmployee_whenPutEmployee_thenReturnUpdatedEmployee() throws Exception {
    given(employeeService.updateEmployee(any(Employee.class))).willReturn(employee);

    ResultActions response =
        mockMvc.perform(put("/api/employees").contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(employee)));

    response.andDo(print()).andExpect(status().isOk())
        .andExpect(jsonPath("$.firstName", is(employee.getFirstName())))
        .andExpect(jsonPath("$.lastName", is(employee.getLastName())))
        .andExpect(jsonPath("$.email", is(employee.getEmail())));
  }

  @Test
  void givenEmployeeId_whenDeleteEmployee_thenReturn200() throws Exception {
    long employeeId = 1L;
    willDoNothing().given(employeeService).deleteEmployee(employeeId);

    ResultActions response = mockMvc.perform(delete("/api/employees/{id}", employeeId));

    response.andExpect(status().isOk()).andDo(print());
  }

}
