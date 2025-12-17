package com.checkout.payment.gateway.exception;

import com.checkout.payment.gateway.model.ErrorResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class CommonExceptionHandler {

  private static final Logger LOG = LoggerFactory.getLogger(CommonExceptionHandler.class);

  @ExceptionHandler(EventProcessingException.class)
  public ResponseEntity<ErrorResponse> handleException(EventProcessingException ex) {
    LOG.info("Attempted to retrieve non-existent payment");
    return new ResponseEntity<>(new ErrorResponse("payment not found"),
        HttpStatus.NOT_FOUND);
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ErrorResponse> handleValidationException(MethodArgumentNotValidException ex) {
    LOG.info("Validation failed: {}", ex.getMessage());
    return new ResponseEntity<>(new ErrorResponse("Validation failed"),
        HttpStatus.BAD_REQUEST);
  }
}
