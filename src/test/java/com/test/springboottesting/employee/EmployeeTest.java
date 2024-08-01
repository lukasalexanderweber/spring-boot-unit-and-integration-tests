package com.test.springboottesting.employee;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import java.util.Locale;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.ValidatorFactory;

/**
 * Unit tests for a class
 */
class EmployeeTest {

  @BeforeEach
  public void setUp() {
    // the violation message is based on the locale and is translated to german on some machines
    Locale.setDefault(Locale.ENGLISH);
  }

  @Test
  void testBuilderAndValidation() {
    var employee = Employee.builder().firstName("Max").email("wrongemail@").build();
    assertEquals("Max", employee.getFirstName());
    assertNull(employee.getLastName());
    assertEquals(0L, employee.getId());

    ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    var validator = factory.getValidator();
    var violations = validator.validate(employee);
    assertThat(violations).hasSize(2);
    assertThat(violations).extracting(ConstraintViolation::getMessage)
        .containsExactlyInAnyOrder("must be a well-formed email address", "must not be blank");
  }
}
