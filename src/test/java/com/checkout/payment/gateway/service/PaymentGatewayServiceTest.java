package com.checkout.payment.gateway.service;

import com.checkout.payment.gateway.model.PostPaymentRequest;
import com.checkout.payment.gateway.util.RejectionMessages;
import jakarta.validation.ConstraintViolation;
import java.util.HashSet;
import java.util.Set;
import jakarta.validation.metadata.ConstraintDescriptor;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PaymentGatewayServiceTest {

    private final PaymentGatewayService paymentGatewayService = new PaymentGatewayService(null, null, null);

    @Test
    void whenViolationsContainMalformedRequestThenReturnMalformedRequest() {
        // Given
        Set<ConstraintViolation<PostPaymentRequest>> violations = createMockViolations(
            RejectionMessages.MALFORMED_REQUEST,
            RejectionMessages.CARD_NUMBER_MALFORMED
        );

        // When
        String result = paymentGatewayService.generateRejectionReason(violations);

        // Then
        assertEquals(RejectionMessages.MALFORMED_REQUEST, result);
    }

    @Test
    void whenSingleViolationThenReturnThatReason() {
        // Given
        Set<ConstraintViolation<PostPaymentRequest>> violations = createMockViolations(
            RejectionMessages.CARD_NUMBER_MALFORMED
        );

        // When
        String result = paymentGatewayService.generateRejectionReason(violations);

        // Then
        assertEquals(RejectionMessages.CARD_NUMBER_MALFORMED, result);
    }

    @Test
    void whenMultipleViolationsThenReturnCombinedReasons() {
        // Given
        Set<ConstraintViolation<PostPaymentRequest>> violations = createMockViolations(
            RejectionMessages.CARD_NUMBER_MALFORMED,
            RejectionMessages.CURRENCY_NOT_SUPPORTED,
            RejectionMessages.CVV_MALFORMED
        );

        // When
        String result = paymentGatewayService.generateRejectionReason(violations);

        // Then
        assertTrue(result.contains(RejectionMessages.CARD_NUMBER_MALFORMED));
        assertTrue(result.contains(RejectionMessages.CURRENCY_NOT_SUPPORTED));
        assertTrue(result.contains(RejectionMessages.CVV_MALFORMED));
        assertTrue(result.contains(", ")); // Contains separator
    }

    @Test
    void whenExpiryFormatErrorThenExcludeExpiredCard() {
        // Given
        Set<ConstraintViolation<PostPaymentRequest>> violations = createMockViolations(
            RejectionMessages.EXPIRY_MONTH_MALFORMED,
            RejectionMessages.EXPIRED_CARD
        );

        // When
        String result = paymentGatewayService.generateRejectionReason(violations);

        // Then
        assertEquals(RejectionMessages.EXPIRY_MONTH_MALFORMED, result);
      assertFalse(result.contains(RejectionMessages.EXPIRED_CARD));
    }

    @Test
    void whenNoExpiryFormatErrorThenIncludeExpiredCard() {
        // Given
        Set<ConstraintViolation<PostPaymentRequest>> violations = createMockViolations(
            RejectionMessages.EXPIRED_CARD,
            RejectionMessages.CARD_NUMBER_MALFORMED
        );

        // When
        String result = paymentGatewayService.generateRejectionReason(violations);

        // Then
        assertTrue(result.contains(RejectionMessages.EXPIRED_CARD));
        assertTrue(result.contains(RejectionMessages.CARD_NUMBER_MALFORMED));
    }

    private Set<ConstraintViolation<PostPaymentRequest>> createMockViolations(String... messages) {
        Set<ConstraintViolation<PostPaymentRequest>> violations = new HashSet<>();
        for (String message : messages) {
            violations.add(new MockConstraintViolation(message));
        }
        return violations;
    }

    private static class MockConstraintViolation implements ConstraintViolation<PostPaymentRequest> {
        private final String message;

        public MockConstraintViolation(String message) {
            this.message = message;
        }

        @Override
        public String getMessage() {
            return message;
        }

        @Override
        public String getMessageTemplate() {
            return null;
        }

        @Override
        public PostPaymentRequest getRootBean() {
            return null;
        }

        @Override
        public Class<PostPaymentRequest> getRootBeanClass() {
            return null;
        }

        @Override
        public Object getLeafBean() {
            return null;
        }

        @Override
        public Object[] getExecutableParameters() {
            return new Object[0];
        }

        @Override
        public Object getExecutableReturnValue() {
            return null;
        }

        @Override
        public jakarta.validation.Path getPropertyPath() {
            return null;
        }

        @Override
        public Object getInvalidValue() {
            return null;
        }

        @Override
        public ConstraintDescriptor<?> getConstraintDescriptor() {
            return null;
        }

        @Override
        public <U> U unwrap(Class<U> type) {
            return null;
        }
    }
}
