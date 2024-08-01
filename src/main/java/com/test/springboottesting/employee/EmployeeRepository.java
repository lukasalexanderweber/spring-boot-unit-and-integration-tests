package com.test.springboottesting.employee;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * The {@link JpaRepository} provides some default queries which can be extended, e.g. by field
 * https://www.baeldung.com/spring-data-jpa-findby-multiple-columns or with custom Queries (JPQL or
 * native)
 */
public interface EmployeeRepository extends JpaRepository<Employee, Long> {

  Optional<Employee> findByEmail(String email);

  // define custom query using JPQL with index params
  @Query("select e from Employee e where e.firstName = ?1 and e.lastName = ?2")
  Optional<Employee> findByJPQL(String firstName, String lastName);

  // define custom query using JPQL with named params
  @Query("select e from Employee e where e.firstName =:firstName and e.lastName =:lastName")
  Optional<Employee> findByJPQLNamedParams(@Param("firstName") String firstName,
      @Param("lastName") String lastName);

  // define custom query using Native SQL with index params
  @Query(value = "select * from employees e where e.first_name =?1 and e.last_name =?2",
      nativeQuery = true)
  Optional<Employee> findByNativeSQL(String firstName, String lastName);

  // define custom query using Native SQL with named params
  @Query(
      value = "select * from employees e where e.first_name =:firstName and e.last_name =:lastName",
      nativeQuery = true)
  Optional<Employee> findByNativeSQLNamed(@Param("firstName") String firstName,
      @Param("lastName") String lastName);
}
