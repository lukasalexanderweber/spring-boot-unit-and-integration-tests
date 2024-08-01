package com.test.springboottesting.exception;

public class DuplicateEmailException extends RuntimeException {

  private static final long serialVersionUID = 1L;

  public DuplicateEmailException(String email) {
    super("Email \"" + email + "\" already exists");
  }
}
