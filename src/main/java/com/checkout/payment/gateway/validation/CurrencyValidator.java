package com.checkout.payment.gateway.validation;

import com.checkout.payment.gateway.enums.Currency;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class CurrencyValidator implements ConstraintValidator<ValidCurrency, String> {

  @Override
  public void initialize(ValidCurrency constraintAnnotation) {}

  @Override
  public boolean isValid(String currency, ConstraintValidatorContext context) {
    if (currency == null) {
      return true; // Let @NotBlank handle null/empty checks
    }

    try {
      Currency.valueOf(currency);
      return true;
    } catch (IllegalArgumentException e) {
      return false;
    }
  }
}
