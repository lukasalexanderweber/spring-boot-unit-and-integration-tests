package com.test.springboottesting.employee;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/**
 * Our central Employee class. We use some validations above the database level. So while we specify
 * on an sql level that a column is nullable, we ensure on the java level with @NotBlank that the
 * field must not be blank. E.g. in the {@link EmployeeController} we can call @Valid if we create
 * an Employee from a Body and fail early if the Body is not a valid Employee. The validation is
 * also tested as a demonstration in EmployeeTest.
 */
@Entity
@Table(name = "employees")
public class Employee {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long id;

  @NotBlank
  @Column(name = "first_name", nullable = false)
  private String firstName;

  @NotBlank
  @Column(name = "last_name", nullable = false)
  private String lastName;

  @NotBlank
  @Email
  @Column(nullable = false)
  private String email;

  public Employee() {}

  public Employee(String firstName, String lastName, String email, long id) {
    super();
    this.firstName = firstName;
    this.lastName = lastName;
    this.email = email;
    this.id = id;
  }

  private Employee(Builder builder) {
    this.id = builder.id;
    this.firstName = builder.firstName;
    this.lastName = builder.lastName;
    this.email = builder.email;
  }

  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }

  public String getFirstName() {
    return firstName;
  }

  public void setFirstName(String firstName) {
    this.firstName = firstName;
  }

  public String getLastName() {
    return lastName;
  }

  public void setLastName(String lastName) {
    this.lastName = lastName;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public static Builder builder() {
    return new Builder();
  }

  public static final class Builder {
    private long id;
    private String firstName;
    private String lastName;
    private String email;

    private Builder() {}

    public Builder id(long id) {
      this.id = id;
      return this;
    }

    public Builder firstName(String firstName) {
      this.firstName = firstName;
      return this;
    }

    public Builder lastName(String lastName) {
      this.lastName = lastName;
      return this;
    }

    public Builder email(String email) {
      this.email = email;
      return this;
    }

    public Employee build() {
      return new Employee(this);
    }
  }
}
