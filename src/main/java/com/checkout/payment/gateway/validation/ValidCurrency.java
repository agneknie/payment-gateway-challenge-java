package com.checkout.payment.gateway.validation;

import com.checkout.payment.gateway.util.RejectionMessages;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = CurrencyValidator.class)
@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidCurrency {

  String message() default RejectionMessages.CURRENCY_NOT_SUPPORTED;

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};
}
