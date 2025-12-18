package com.checkout.payment.gateway.validation;

import com.checkout.payment.gateway.util.RejectionMessages;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = ExpiryDateValidator.class)
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidExpiryDate {

  String message() default RejectionMessages.EXPIRED_CARD;

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};
}
