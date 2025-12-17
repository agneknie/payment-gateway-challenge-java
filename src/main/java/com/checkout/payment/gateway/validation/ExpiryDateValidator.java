package com.checkout.payment.gateway.validation;

import com.checkout.payment.gateway.model.PostPaymentRequest;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.time.LocalDate;
import java.time.YearMonth;

public class ExpiryDateValidator implements ConstraintValidator<ValidExpiryDate, PostPaymentRequest> {

  @Override
  public void initialize(ValidExpiryDate constraintAnnotation) {}

  @Override
  public boolean isValid(PostPaymentRequest request, ConstraintValidatorContext context) {
    if (request.getExpiryMonth() == null || request.getExpiryYear() == null) {
      return true; // Let @NotNull handle null checks
    }

    try {
      // Handle 2-digit years by converting to 4-digit (assume 2000-2099)
      int fullYear = request.getExpiryYear() < 100 ? request.getExpiryYear() + 2000 : request.getExpiryYear();
      YearMonth expiryDate = YearMonth.of(fullYear, request.getExpiryMonth());
      YearMonth currentMonth = YearMonth.now();

      return expiryDate.isAfter(currentMonth);
    } catch (Exception e) {
      return false; // Invalid date
    }
  }
}
