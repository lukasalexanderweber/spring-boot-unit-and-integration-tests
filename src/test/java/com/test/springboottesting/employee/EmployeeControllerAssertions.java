package com.test.springboottesting.employee;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.assertj.core.api.AbstractAssert;
import org.hamcrest.CoreMatchers;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.ResultHandler;

/**
 * These are some reusable assertions for {@link ResultActions}.
 */
public class EmployeeControllerAssertions
    extends AbstractAssert<EmployeeControllerAssertions, ResultActions> {

  private EmployeeControllerAssertions(ResultActions actual) {
    super(actual, EmployeeControllerAssertions.class);
  }

  static EmployeeControllerAssertions assertThat(ResultActions actual) {
    return new EmployeeControllerAssertions(actual);
  }

  EmployeeControllerAssertions hasStatus(HttpStatus status) throws Exception {
    actual.andExpect(status().is(status.value()));
    return this;
  }

  EmployeeControllerAssertions hasMediaType(MediaType mediaType) throws Exception {
    actual.andExpect(content().contentType(mediaType));
    return this;
  }

  EmployeeControllerAssertions hasSize(int size) throws Exception {
    actual.andExpect(jsonPath("$.size()", CoreMatchers.is(size)));
    return this;
  }

  EmployeeControllerAssertions hasEmployeeInBody(Employee employee) throws Exception {
    actual.andDo(checkEmployee(employee));
    return this;
  }

  static ResultHandler checkEmployee(Employee employee) {
    return result -> {
      jsonPath("$.firstName", CoreMatchers.is(employee.getFirstName())).match(result);
      jsonPath("$.lastName", CoreMatchers.is(employee.getLastName())).match(result);
      jsonPath("$.email", CoreMatchers.is(employee.getEmail())).match(result);
      if (employee.getId() > 0)
        jsonPath("$.id", CoreMatchers.is(employee.getId()), Long.class).match(result);
    };
  }

  EmployeeControllerAssertions hasProblemJsonStatus(HttpStatus status) throws Exception {
    actual.andExpect(jsonPath("$.status", CoreMatchers.is(status.value())));
    return this;
  }
  
  EmployeeControllerAssertions hasProblemJsonDetail(String detail) throws Exception {
    actual.andExpect(jsonPath("$.detail", CoreMatchers.is(detail)));
    return this;
  }
}
