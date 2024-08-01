package com.test.springboottesting.exception;

import java.util.HashMap;
import java.util.Map;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import com.test.springboottesting.employee.EmployeeController;

/**
 * This class is responsible to react on errors during calls on our controller(s). Depending on the
 * error, a standardized error response is created (https://datatracker.ietf.org/doc/html/rfc7807,
 * media type: application/problem+json). With this class, we can keep exception logic in our
 * controller class (e.g. {@link EmployeeController}) to a minimum. Of course in a real world
 * scenario we would add logging here
 */
@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

  @ExceptionHandler(Exception.class)
  public ResponseEntity<Object> handleGlobalException(Exception ex, WebRequest request) {
    ex.printStackTrace(); // log this of cause :)
    return handleExceptionInternal(ex, HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(),
        new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR, request);
  }

  @ExceptionHandler(ResourceNotFoundException.class)
  public ResponseEntity<Object> handleResourceNotFoundException(ResourceNotFoundException ex,
      WebRequest request) {
    ProblemDetail body =
        createProblemDetail(ex, HttpStatus.NOT_FOUND, ex.getMessage(), null, null, request);
    return handleExceptionInternal(ex, body, new HttpHeaders(), HttpStatus.NOT_FOUND, request);
  }

  @ExceptionHandler(DuplicateEmailException.class)
  public ResponseEntity<Object> handleDuplicateEmail(DuplicateEmailException ex,
      WebRequest request) {
    ProblemDetail body =
        createProblemDetail(ex, HttpStatus.BAD_REQUEST, ex.getMessage(), null, null, request);
    return handleExceptionInternal(ex, body, new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
  }

  /**
   * The ResponseEntityExceptionHandler which is inherited from our GlobalExceptionHandler already
   * handles MethodArgumentNotValidExceptions. So here we must overwrite the behavior if we want to
   * provide a bit more information (the default impl does not contain as much helpful information)
   */
  @Override
  protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
      HttpHeaders headers, HttpStatusCode status, WebRequest request) {
    Map<String, String> errors = new HashMap<>();
    ex.getBindingResult().getAllErrors().forEach(error -> {
      String fieldName = ((FieldError) error).getField();
      String errorMessage = error.getDefaultMessage();
      errors.put(fieldName, errorMessage);
    });
    String detail = "Invalid Field(s): " + errors.toString();
    ProblemDetail body =
        createProblemDetail(ex, HttpStatus.BAD_REQUEST, detail, null, null, request);
    return handleExceptionInternal(ex, body, new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
  }
}
