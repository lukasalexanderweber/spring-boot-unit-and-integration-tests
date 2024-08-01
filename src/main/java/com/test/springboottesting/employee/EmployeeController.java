package com.test.springboottesting.employee;

import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/employees")
public class EmployeeController {

  private EmployeeService employeeService;

  public EmployeeController(EmployeeService employeeService) {
    this.employeeService = employeeService;
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public Employee createEmployee(@Valid @RequestBody Employee employee) {
    return employeeService.saveEmployee(employee);
  }

  @GetMapping
  public List<Employee> getAllEmployees() {
    return employeeService.getAllEmployees();
  }

  @GetMapping("{id}")
  public ResponseEntity<Employee> getEmployeeById(@PathVariable("id") long employeeId) {
    // we could also throw an exception here! It would be more standardized to have a problem+json
    // created by the GlobalExceptionHandler. I just kept it because it shows how to map an Optional
    // into a ResponseEntity
    return employeeService.getEmployeeById(employeeId).map(ResponseEntity::ok)
        .orElseGet(() -> ResponseEntity.notFound().build());
  }

  @PutMapping()
  public Employee updateEmployee(@Valid @RequestBody Employee employee) {
    return employeeService.updateEmployee(employee);
  }

  @DeleteMapping("{id}")
  public ResponseEntity<String> deleteEmployee(@PathVariable("id") long employeeId) {

    employeeService.deleteEmployee(employeeId);

    return new ResponseEntity<>("Employee deleted successfully!", HttpStatus.OK);

  }
}
