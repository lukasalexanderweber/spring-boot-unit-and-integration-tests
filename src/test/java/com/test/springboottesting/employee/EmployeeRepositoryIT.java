package com.test.springboottesting.employee;

import static org.assertj.core.api.Assertions.assertThat;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

/**
 * a {@link DataJpaTest} as {@link EmployeeRespositoryTest} but against a postgres database.
 */
@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
class EmployeeRepositoryIT extends AbstractPostgresIT {

  @Autowired
  private EmployeeRepository employeeRepository;

  private Employee employee;

  @BeforeEach
  void setUp() {
    employeeRepository.deleteAll();
    employee =
        Employee.builder().firstName("John").lastName("Doe").email("johndoe@email.com").build();
  }

  @Test
  void givenEmployeeObject_whenSave_thenEmployeeObjectHasId() {
    employeeRepository.save(employee);

    assertThat(employee.getId()).isPositive();
  }

  @Test
  void givenEmployeeObject_whenSave_thenReturnSavedEmployee() {
    Employee savedEmployee = employeeRepository.save(employee);

    assertThat(savedEmployee).isNotNull();
    assertThat(savedEmployee.getId()).isPositive();
  }

  @Test
  void givenMultipleSavedEmployees_whenFindAll_thenEmployeesList() {
    Employee employee2 =
        Employee.builder().firstName("Jane").lastName("Roe").email("janeroe@email.com").build();
    employeeRepository.save(employee);
    employeeRepository.save(employee2);

    List<Employee> employeeList = employeeRepository.findAll();

    assertThat(employeeList).isNotNull().hasSize(2);
  }

  @Test
  void givenSavedEmployee_whenFindById_thenReturnEmployeeObject() {
    employeeRepository.save(employee);

    Employee employeeDB = employeeRepository.findById(employee.getId()).get();

    assertThat(employeeDB).isNotNull();
  }

  @Test
  void givenSavedEmployee_whenFindByEmail_thenReturnEmployeeObject() {
    employeeRepository.save(employee);

    Employee employeeDB = employeeRepository.findByEmail(employee.getEmail()).get();

    assertThat(employeeDB).isNotNull();
  }

  @Test
  void givenSavedEmployee_whenSaveUpdatedEmployee_thenReturnUpdatedEmployee() {
    employeeRepository.save(employee);

    Employee savedEmployee = employeeRepository.findById(employee.getId()).get();
    savedEmployee.setEmail("john@email.com");
    Employee updatedEmployee = employeeRepository.save(savedEmployee);

    assertThat(updatedEmployee.getEmail()).isEqualTo("john@email.com");
  }

  @Test
  void givenSavedEmployee_whenDelete_thenRemoveEmployee() {
    employeeRepository.save(employee);

    employeeRepository.deleteById(employee.getId());
    Optional<Employee> employeeOptional = employeeRepository.findById(employee.getId());

    assertThat(employeeOptional).isEmpty();
  }

  @Test
  void givenSavedEmployee_whenFindByJPQL_thenReturnEmployeeObject() {
    employeeRepository.save(employee);

    Employee savedEmployee =
        employeeRepository.findByJPQL(employee.getFirstName(), employee.getLastName()).get();

    assertThat(savedEmployee).isNotNull();
  }

  @Test
  void givenSavedEmployee_whenFindByJPQLNamedParams_thenReturnEmployeeObject() {
    employeeRepository.save(employee);

    Employee savedEmployee = employeeRepository
        .findByJPQLNamedParams(employee.getFirstName(), employee.getLastName()).get();

    assertThat(savedEmployee).isNotNull();
  }

  @Test
  void givenSavedEmployee_whenFindByNativeSQL_thenReturnEmployeeObject() {
    employeeRepository.save(employee);

    Employee savedEmployee =
        employeeRepository.findByNativeSQL(employee.getFirstName(), employee.getLastName()).get();

    assertThat(savedEmployee).isNotNull();
  }

  @Test
  void givenSavedEmployee_whenFindByNativeSQLNamedParams_thenReturnEmployeeObject() {
    employeeRepository.save(employee);

    Employee savedEmployee = employeeRepository
        .findByNativeSQLNamed(employee.getFirstName(), employee.getLastName()).get();

    assertThat(savedEmployee).isNotNull();
  }
}
