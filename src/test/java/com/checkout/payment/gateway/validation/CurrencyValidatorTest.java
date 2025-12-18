package com.checkout.payment.gateway.validation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class CurrencyValidatorTest {

    private CurrencyValidator validator;

    @BeforeEach
    void setUp() {
        validator = new CurrencyValidator();
    }

    @Test
    void whenValidCurrencyUSD_thenReturnTrue() {
        // When
        boolean result = validator.isValid("USD", null);

        // Then
        assertTrue(result);
    }

    @Test
    void whenValidCurrencyGBP_thenReturnTrue() {
        // When
        boolean result = validator.isValid("GBP", null);

        // Then
        assertTrue(result);
    }

    @Test
    void whenValidCurrencyEUR_thenReturnTrue() {
        // When
        boolean result = validator.isValid("EUR", null);

        // Then
        assertTrue(result);
    }

    @Test
    void whenInvalidCurrency_thenReturnFalse() {
        // When
        boolean result = validator.isValid("INVALID", null);

        // Then
        assertFalse(result);
    }

    @Test
    void whenNullCurrency_thenReturnTrue() {
        // When
        boolean result = validator.isValid(null, null);

        // Then
        assertTrue(result);
    }

    @Test
    void whenEmptyString_thenReturnFalse() {
        // When
        boolean result = validator.isValid("", null);

        // Then
        assertFalse(result);
    }
}
