package com.test.springboottesting.employee;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import com.test.springboottesting.exception.DuplicateEmailException;
import com.test.springboottesting.exception.ResourceNotFoundException;

/**
 * Unit testing {@link EmployeeServiceImpl} by mocking all {@link EmployeeRepository} dependencies
 */
@ExtendWith(MockitoExtension.class)
class EmployeeServiceTest {

  @Mock
  private EmployeeRepository employeeRepository;

  @InjectMocks
  private EmployeeService employeeService;

  private Employee employee;

  @BeforeEach
  void setup() {
    employee = employee();
  }

  private Employee employee() {
    return Employee.builder().id(1L).firstName("John").lastName("Doe").email("johndoe@email.com")
        .build();
  }

  @Test
  void givenEmployeeObject_whenSaveEmployee_thenReturnEmployeeObject() {
    given(employeeRepository.findByEmail(employee.getEmail())).willReturn(Optional.empty());
    given(employeeRepository.save(employee)).willReturn(employee);

    Employee savedEmployee = employeeService.saveEmployee(employee);

    assertThat(savedEmployee.getId()).isPositive();
  }

  @Test
  void givenExistingEmail_whenSaveEmployee_thenThrowsException() {
    given(employeeRepository.findByEmail(employee.getEmail())).willReturn(Optional.of(employee));

    Exception exception =
        assertThrows(DuplicateEmailException.class, () -> employeeService.saveEmployee(employee));

    assertThat(exception).hasMessageEndingWith("already exists");
    verify(employeeRepository, never()).save(any(Employee.class));
  }

  @Test
  void givenEmployeesList_whenGetAllEmployees_thenReturnEmployeesList() {
    Employee employee2 = Employee.builder().id(2L).firstName("Jane").lastName("Roe")
        .email("janeroe@email.com").build();
    given(employeeRepository.findAll()).willReturn(List.of(employee, employee2));

    List<Employee> employeeList = employeeService.getAllEmployees();

    assertThat(employeeList).isNotNull().hasSize(2);
    assertThat(employeeList).extracting("firstName").containsExactlyInAnyOrder("John", "Jane");
  }

  @Test
  void givenEmptyEmployeesList_whenGetAllEmployees_thenReturnEmptyEmployeesList() {
    given(employeeRepository.findAll()).willReturn(Collections.emptyList());

    List<Employee> employeeList = employeeService.getAllEmployees();

    assertThat(employeeList).isEmpty();
  }

  @Test
  void givenEmployeeId_whenGetEmployeeById_thenReturnEmployeeObject() {
    given(employeeRepository.findById(1L)).willReturn(Optional.of(employee));

    Employee savedEmployee = employeeService.getEmployeeById(employee.getId()).get();

    assertThat(savedEmployee).isNotNull();
  }

  @Test
  void givenSavedEmployee_whenUpdateEmployee_thenReturnUpdatedEmployee() {
    given(employeeRepository.save(employee)).willReturn(employee);
    given(employeeRepository.findById(1L)).willReturn(Optional.of(employee));

    employee.setEmail("john@email.com");
    Employee updatedEmployee = employeeService.updateEmployee(employee);

    assertThat(updatedEmployee.getEmail()).isEqualTo("john@email.com");
  }

  @Test
  void givenNotExistingEmployee_whenUpdateEmployee_thenThrowsException() {
    Exception exception = assertThrows(ResourceNotFoundException.class,
        () -> employeeService.updateEmployee(employee));

    assertThat(exception)
        .hasMessageStartingWith("The Employee to update does not exist in the database yet (id=");
    verify(employeeRepository, never()).save(any(Employee.class));
  }

  @Test
  void givenUpdatedEmailAlreadyExists_whenUpdateEmployee_thenThrowsException() {
    Employee employee2 = Employee.builder().id(2L).firstName("Jane").lastName("Roe")
        .email("janeroe@email.com").build();
    given(employeeRepository.findById(1L)).willReturn(Optional.of(employee));
    given(employeeRepository.findByEmail(employee2.getEmail())).willReturn(Optional.of(employee2));

    // Note: we can't update "employee", since then the findById mock would return an
    // "oldEmployee" which would already have the new email
    Employee employeeToUpdate = employee();
    employeeToUpdate.setEmail(employee2.getEmail());
    Exception exception = assertThrows(DuplicateEmailException.class,
        () -> employeeService.updateEmployee(employeeToUpdate));

    assertThat(exception).hasMessageEndingWith("already exists");
    verify(employeeRepository, never()).save(any(Employee.class));
  }

  @Test
  void givenEmployeeId_whenDeleteEmployee_thenNothing() {
    long employeeId = 1L;
    willDoNothing().given(employeeRepository).deleteById(employeeId);

    employeeService.deleteEmployee(employeeId);

    verify(employeeRepository, times(1)).deleteById(employeeId);
  }
}
