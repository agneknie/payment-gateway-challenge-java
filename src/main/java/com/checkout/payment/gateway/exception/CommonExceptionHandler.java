package com.checkout.payment.gateway.exception;

import com.checkout.payment.gateway.model.ErrorResponse;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class CommonExceptionHandler {

  private static final Logger LOG = LoggerFactory.getLogger(CommonExceptionHandler.class);

  @ExceptionHandler(EventProcessingException.class)
  public ResponseEntity<ErrorResponse> handleException() {
    LOG.info("Attempted to retrieve non-existent payment");
    return new ResponseEntity<>(new ErrorResponse("payment not found"),
        HttpStatus.NOT_FOUND);
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ErrorResponse> handleValidationException(MethodArgumentNotValidException ex) {
    LOG.info("Validation failed: {}", ex.getMessage());
    return new ResponseEntity<>(new ErrorResponse("validation failed"),
        HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(MethodArgumentTypeMismatchException.class)
  public ResponseEntity<ErrorResponse> handleTypeMismatchException(MethodArgumentTypeMismatchException ex) {
    LOG.info("Type mismatch for parameter: {}", ex.getName());
    if ("id".equals(ex.getName()) && UUID.class.equals(ex.getRequiredType())) {
      return new ResponseEntity<>(new ErrorResponse("invalid payment id"),
          HttpStatus.BAD_REQUEST);
    }
    return new ResponseEntity<>(new ErrorResponse("invalid parameter"),
        HttpStatus.BAD_REQUEST);
  }
}
