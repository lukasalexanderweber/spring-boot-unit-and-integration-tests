package com.test.springboottesting.employee;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestComponent;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * These are some reusable {@link MockMvc} requests to test an {@link EmployeeController}.
 */
@TestComponent
public class EmployeeControllerRequests {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  ResultActions postEmployee(Employee employee) throws Exception {
    return mockMvc.perform(post("/api/employees").contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(employee)));
  }

  ResultActions putEmployee(Employee employee) throws Exception {
    return mockMvc.perform(put("/api/employees").contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(employee)));
  }

  ResultActions getEmployees() throws Exception {
    return mockMvc.perform(get("/api/employees"));
  }

  ResultActions getEmployee(long id) throws Exception {
    return mockMvc.perform(get("/api/employees/{id}", id));
  }
}
