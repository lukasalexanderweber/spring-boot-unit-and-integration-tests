package com.test.springboottesting.employee;

import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.test.springboottesting.exception.DuplicateEmailException;
import com.test.springboottesting.exception.ResourceNotFoundException;

/**
 * This class contains the business logic of our employee management system. At the moment the only
 * business logic is that an email must not exist multiple times
 */
@Service
public class EmployeeService {

  private EmployeeRepository employeeRepository;

  @Autowired // could be removed:
             // https://stackoverflow.com/questions/41092751/spring-injects-dependencies-in-constructor-without-autowired-annotation
  public EmployeeService(EmployeeRepository employeeRepository) {
    this.employeeRepository = employeeRepository;
  }

  public Employee saveEmployee(Employee employee) {
    validateThatEmailDoesNotExist(employee.getEmail());
    return employeeRepository.save(employee);
  }

  public List<Employee> getAllEmployees() {
    return employeeRepository.findAll();
  }

  public Optional<Employee> getEmployeeById(long id) {
    return employeeRepository.findById(id);
  }

  public Employee updateEmployee(Employee updatedEmployee) {
    Employee oldEmployee =
        getEmployeeById(updatedEmployee.getId()).orElseThrow(() -> new ResourceNotFoundException(
            "The Employee to update does not exist in the database yet (id="
                + updatedEmployee.getId() + ")"));

    if (!oldEmployee.getEmail().equals(updatedEmployee.getEmail()))
      validateThatEmailDoesNotExist(updatedEmployee.getEmail());

    return employeeRepository.save(updatedEmployee);
  }

  public void deleteEmployee(long id) {
    employeeRepository.deleteById(id);
  }

  private void validateThatEmailDoesNotExist(String email) {
    if (employeeRepository.findByEmail(email).isPresent())
      throw new DuplicateEmailException(email);
  }

}

