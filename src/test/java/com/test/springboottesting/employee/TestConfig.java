package com.test.springboottesting.employee;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.ComponentScan;

@TestConfiguration
@ComponentScan(basePackages = "init.springboottesting.employee;")
class TestConfig {

}
