package com.test.springboottesting.employee;

import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

/**
 * A Singleton Container as described in
 * https://testcontainers.com/guides/testcontainers-container-lifecycle/. Please read the guide in
 * order to get familiar with the test container lifecycle, it's easy to get messed up with xD.
 */
public abstract class AbstractPostgresIT {
  static PostgreSQLContainer<?> POSTGRES_CONTAINER =
      new PostgreSQLContainer<>(DockerImageName.parse("postgres:16")).withDatabaseName("ems")
          .withUsername("username").withPassword("password");

  static {
    POSTGRES_CONTAINER.start();
  }

  /**
   * this function overwrites the settings of application.properties so that
   * {@link EmployeeRepository} connects to the postgres database. see also
   * https://www.docker.com/blog/spring-boot-application-testing-and-development-with-testcontainers/
   */
  @DynamicPropertySource
  public static void dynamicPropertySource(DynamicPropertyRegistry registry) {
    registry.add("spring.datasource.url", POSTGRES_CONTAINER::getJdbcUrl);
    registry.add("spring.datasource.username", POSTGRES_CONTAINER::getUsername);
    registry.add("spring.datasource.password", POSTGRES_CONTAINER::getPassword);
    // for h2 tests create-drop is selected per default, for a production database (postgres) we
    // need to set it per hand. create-drop ensures that the database schema is created fresh at the
    // start of the application and dropped when the application shuts down
    // https://docs.spring.io/spring-boot/docs/1.1.0.M1/reference/html/howto-database-initialization.html
    registry.add("spring.jpa.hibernate.ddl-auto", () -> "create-drop");
  }
}
