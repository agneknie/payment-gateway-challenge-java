package com.checkout.payment.gateway.validation;

import com.checkout.payment.gateway.model.PostPaymentRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ExpiryDateValidatorTest {

    private ExpiryDateValidator validator;
    private PostPaymentRequest request;

    @BeforeEach
    void setUp() {
        validator = new ExpiryDateValidator();
        request = new PostPaymentRequest();
    }

    @Test
    void whenNullExpiryMonth_thenReturnTrue() {
        // Given
        request.setExpiryMonth(null);
        request.setExpiryYear(2026);

        // When
        boolean result = validator.isValid(request, null);

        // Then
        assertTrue(result);
    }

    @Test
    void whenNullExpiryYear_thenReturnTrue() {
        // Given
        request.setExpiryMonth(12);
        request.setExpiryYear(null);

        // When
        boolean result = validator.isValid(request, null);

        // Then
        assertTrue(result);
    }

    @Test
    void whenFutureDate_thenReturnTrue() {
        // Given - January 2026 (assuming current is December 2025)
        request.setExpiryMonth(1);
        request.setExpiryYear(2026);

        // When
        boolean result = validator.isValid(request, null);

        // Then
        assertTrue(result);
    }

    @Test
    void whenPastDate_thenReturnFalse() {
        // Given - November 2025 (assuming current is December 2025)
        request.setExpiryMonth(11);
        request.setExpiryYear(2025);

        // When
        boolean result = validator.isValid(request, null);

        // Then
        assertFalse(result);
    }

    @Test
    void whenCurrentMonth_thenReturnFalse() {
        // Given - December 2025 (assuming current is December 2025)
        request.setExpiryMonth(12);
        request.setExpiryYear(2025);

        // When
        boolean result = validator.isValid(request, null);

        // Then
        assertFalse(result);
    }

    @Test
    void whenInvalidMonth_thenReturnFalse() {
        // Given
        request.setExpiryMonth(13);
        request.setExpiryYear(2026);

        // When
        boolean result = validator.isValid(request, null);

        // Then
        assertFalse(result);
    }

    @Test
    void whenInvalidYear_thenReturnFalse() {
        // Given
        request.setExpiryMonth(12);
        request.setExpiryYear(-1);

        // When
        boolean result = validator.isValid(request, null);

        // Then
        assertFalse(result);
    }

    @Test
    void whenTwoDigitYear_thenConvertAndValidate() {
        // Given - Year 30 should become 2030
        request.setExpiryMonth(1);
        request.setExpiryYear(30);

        // When
        boolean result = validator.isValid(request, null);

        // Then
        assertTrue(result);
    }

    @Test
    void whenFourDigitYear_thenUseAsIs() {
        // Given
        request.setExpiryMonth(1);
        request.setExpiryYear(2030);

        // When
        boolean result = validator.isValid(request, null);

        // Then
        assertTrue(result);
    }
}
