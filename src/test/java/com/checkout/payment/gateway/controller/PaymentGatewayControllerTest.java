package com.checkout.payment.gateway.controller;


import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.checkout.payment.gateway.enums.PaymentStatus;
import com.checkout.payment.gateway.model.SuccessfulPaymentResponse;
import com.checkout.payment.gateway.repository.PaymentsRepository;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

@SpringBootTest
@AutoConfigureMockMvc
class PaymentGatewayControllerTest {

  @Autowired
  private MockMvc mvc;
  @Autowired
  PaymentsRepository paymentsRepository;

  @Test
  void whenPaymentWithIdExistThenCorrectPaymentIsReturned() throws Exception {
    SuccessfulPaymentResponse payment = new SuccessfulPaymentResponse(
        UUID.randomUUID(),
        PaymentStatus.AUTHORIZED,
        "4321",
        12,
        2024,
        "USD",
        10
    );

    paymentsRepository.add(payment);

    mvc.perform(MockMvcRequestBuilders.get("/payment/" + payment.getId()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.status").value(payment.getStatus().getName()))
        .andExpect(jsonPath("$.cardNumberLastFour").value(payment.getCardNumberLastFour()))
        .andExpect(jsonPath("$.expiryMonth").value(payment.getExpiryMonth()))
        .andExpect(jsonPath("$.expiryYear").value(payment.getExpiryYear()))
        .andExpect(jsonPath("$.currency").value(payment.getCurrency()))
        .andExpect(jsonPath("$.amount").value(payment.getAmount()));
  }

  @Test
  void whenPaymentWithIdDoesNotExistThen404IsReturned() throws Exception {
    mvc.perform(MockMvcRequestBuilders.get("/payment/" + UUID.randomUUID()))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.message").value("Page not found"));
  }

  @Test
  void whenValidPaymentRequestIsSubmittedThenAuthorizedResponseIsReturned() throws Exception {
    String requestJson = """
        {
          "card_number": "4111111111111111",
          "expiry_month": 1,
          "expiry_year": 26,
          "currency": "USD",
          "amount": 1000,
          "cvv": "123"
        }
        """;

    mvc.perform(MockMvcRequestBuilders.post("/payment")
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestJson))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").exists())
        .andExpect(jsonPath("$.status").value("Authorized"))
        .andExpect(jsonPath("$.cardNumberLastFour").value("1111"))
        .andExpect(jsonPath("$.expiryMonth").value(1))
        .andExpect(jsonPath("$.expiryYear").value(26))
        .andExpect(jsonPath("$.currency").value("USD"))
        .andExpect(jsonPath("$.amount").value(1000));
  }

  // Currency validation tests
  @Test
  void whenCurrencyIsInvalidThenRejected() throws Exception {
    String requestJson = """
        {
          "card_number": "4111111111111111",
          "expiry_month": 1,
          "expiry_year": 26,
          "currency": "CAD",
          "amount": 1000,
          "cvv": "123"
        }
        """;

    mvc.perform(MockMvcRequestBuilders.post("/payment")
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestJson))
        .andExpect(status().isUnprocessableEntity())
        .andExpect(jsonPath("$.id").doesNotExist())
        .andExpect(jsonPath("$.status").value("Rejected"))
        .andExpect(jsonPath("$.rejectionReason").value("Currency is not supported"));
  }

  @Test
  void whenCurrencyIsNotThreeCharactersThenRejected() throws Exception {
    String requestJson = """
        {
          "card_number": "4111111111111111",
          "expiry_month": 1,
          "expiry_year": 26,
          "currency": "US",
          "amount": 1000,
          "cvv": "123"
        }
        """;

    mvc.perform(MockMvcRequestBuilders.post("/payment")
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestJson))
        .andExpect(status().isUnprocessableEntity())
        .andExpect(jsonPath("$.id").doesNotExist())
        .andExpect(jsonPath("$.status").value("Rejected"))
        .andExpect(jsonPath("$.rejectionReason").value("Currency is not supported"));
  }

  @Test
  void whenCurrencyIsMissingThenRejected() throws Exception {
    String requestJson = """
        {
          "card_number": "4111111111111111",
          "expiry_month": 1,
          "expiry_year": 26,
          "amount": 1000,
          "cvv": "123"
        }
        """;

    mvc.perform(MockMvcRequestBuilders.post("/payment")
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestJson))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.id").doesNotExist())
        .andExpect(jsonPath("$.status").value("Rejected"))
        .andExpect(jsonPath("$.rejectionReason").value("malformed request"));
  }

  // Card number validation tests
  @Test
  void whenCardNumberIsThirteenCharactersThenRejected() throws Exception {
    String requestJson = """
        {
          "card_number": "4111111111111",
          "expiry_month": 1,
          "expiry_year": 26,
          "currency": "USD",
          "amount": 1000,
          "cvv": "123"
        }
        """;

    mvc.perform(MockMvcRequestBuilders.post("/payment")
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestJson))
        .andExpect(status().isUnprocessableEntity())
        .andExpect(jsonPath("$.id").doesNotExist())
        .andExpect(jsonPath("$.status").value("Rejected"))
        .andExpect(jsonPath("$.rejectionReason").value("cardNumber is malformed/incorrect"));
  }

  @Test
  void whenCardNumberIsTwentyCharactersThenRejected() throws Exception {
    String requestJson = """
        {
          "card_number": "41111111111111111111",
          "expiry_month": 1,
          "expiry_year": 26,
          "currency": "USD",
          "amount": 1000,
          "cvv": "123"
        }
        """;

    mvc.perform(MockMvcRequestBuilders.post("/payment")
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestJson))
        .andExpect(status().isUnprocessableEntity())
        .andExpect(jsonPath("$.id").doesNotExist())
        .andExpect(jsonPath("$.status").value("Rejected"))
        .andExpect(jsonPath("$.rejectionReason").value("cardNumber is malformed/incorrect"));
  }

  @Test
  void whenCardNumberHasNonNumericCharactersThenRejected() throws Exception {
    String requestJson = """
        {
          "card_number": "411111111111111a",
          "expiry_month": 1,
          "expiry_year": 26,
          "currency": "USD",
          "amount": 1000,
          "cvv": "123"
        }
        """;

    mvc.perform(MockMvcRequestBuilders.post("/payment")
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestJson))
        .andExpect(status().isUnprocessableEntity())
        .andExpect(jsonPath("$.id").doesNotExist())
        .andExpect(jsonPath("$.status").value("Rejected"))
        .andExpect(jsonPath("$.rejectionReason").value("cardNumber is malformed/incorrect"));
  }

  @Test
  void whenCardNumberIsMissingThenRejected() throws Exception {
    String requestJson = """
        {
          "expiry_month": 1,
          "expiry_year": 26,
          "currency": "USD",
          "amount": 1000,
          "cvv": "123"
        }
        """;

    mvc.perform(MockMvcRequestBuilders.post("/payment")
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestJson))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.id").doesNotExist())
        .andExpect(jsonPath("$.status").value("Rejected"))
        .andExpect(jsonPath("$.rejectionReason").value("malformed request"));
  }

  // Expiry validation tests
  @Test
  void whenExpiryMonthIsThirteenThenRejected() throws Exception {
    String requestJson = """
        {
          "card_number": "4111111111111111",
          "expiry_month": 13,
          "expiry_year": 26,
          "currency": "USD",
          "amount": 1000,
          "cvv": "123"
        }
        """;

    mvc.perform(MockMvcRequestBuilders.post("/payment")
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestJson))
        .andExpect(status().isUnprocessableEntity())
        .andExpect(jsonPath("$.id").doesNotExist())
        .andExpect(jsonPath("$.status").value("Rejected"))
        .andExpect(jsonPath("$.rejectionReason").value("expiryMonth is malformed/incorrect"));
  }

  @Test
  void whenExpiryMonthIsZeroThenRejected() throws Exception {
    String requestJson = """
        {
          "card_number": "4111111111111111",
          "expiry_month": 0,
          "expiry_year": 26,
          "currency": "USD",
          "amount": 1000,
          "cvv": "123"
        }
        """;

    mvc.perform(MockMvcRequestBuilders.post("/payment")
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestJson))
        .andExpect(status().isUnprocessableEntity())
        .andExpect(jsonPath("$.id").doesNotExist())
        .andExpect(jsonPath("$.status").value("Rejected"))
        .andExpect(jsonPath("$.rejectionReason").value("expiryMonth is malformed/incorrect"));
  }

  @Test
  void whenExpiryMonthIsMissingThenRejected() throws Exception {
    String requestJson = """
        {
          "card_number": "4111111111111111",
          "expiry_year": 26,
          "currency": "USD",
          "amount": 1000,
          "cvv": "123"
        }
        """;

    mvc.perform(MockMvcRequestBuilders.post("/payment")
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestJson))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.id").doesNotExist())
        .andExpect(jsonPath("$.status").value("Rejected"))
        .andExpect(jsonPath("$.rejectionReason").value("malformed request"));
  }

  @Test
  void whenExpiryYearIsMissingThenRejected() throws Exception {
    String requestJson = """
        {
          "card_number": "4111111111111111",
          "expiry_month": 1,
          "currency": "USD",
          "amount": 1000,
          "cvv": "123"
        }
        """;

    mvc.perform(MockMvcRequestBuilders.post("/payment")
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestJson))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.id").doesNotExist())
        .andExpect(jsonPath("$.status").value("Rejected"))
        .andExpect(jsonPath("$.rejectionReason").value("malformed request"));
  }

  @Test
  void whenExpiryDateIsInThePastThenRejected() throws Exception {
    String requestJson = """
        {
          "card_number": "4111111111111111",
          "expiry_month": 1,
          "expiry_year": 24,
          "currency": "USD",
          "amount": 1000,
          "cvv": "123"
        }
        """;

    mvc.perform(MockMvcRequestBuilders.post("/payment")
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestJson))
        .andExpect(status().isUnprocessableEntity())
        .andExpect(jsonPath("$.id").doesNotExist())
        .andExpect(jsonPath("$.status").value("Rejected"))
        .andExpect(jsonPath("$.rejectionReason").value("Expired card"));
  }

  @Test
  void whenExpiryYearIsFourDigitsThenRejected() throws Exception {
    String requestJson = """
        {
          "card_number": "4111111111111111",
          "expiry_month": 1,
          "expiry_year": 2026,
          "currency": "USD",
          "amount": 1000,
          "cvv": "123"
        }
        """;

    mvc.perform(MockMvcRequestBuilders.post("/payment")
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestJson))
        .andExpect(status().isUnprocessableEntity())
        .andExpect(jsonPath("$.id").doesNotExist())
        .andExpect(jsonPath("$.status").value("Rejected"))
        .andExpect(jsonPath("$.rejectionReason").value("expiryYear is malformed/incorrect"));
  }

  // Amount validation tests
  @Test
  void whenAmountIsNotAnIntegerThenRejected() throws Exception {
    String requestJson = """
        {
          "card_number": "4111111111111111",
          "expiry_month": 1,
          "expiry_year": 26,
          "currency": "USD",
          "amount": "100.50",
          "cvv": "123"
        }
        """;

    mvc.perform(MockMvcRequestBuilders.post("/payment")
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestJson))
        .andExpect(status().isUnprocessableEntity())
        .andExpect(jsonPath("$.id").doesNotExist())
        .andExpect(jsonPath("$.status").value("Rejected"))
        .andExpect(jsonPath("$.rejectionReason").value("amount is malformed/incorrect"));
  }

  @Test
  void whenAmountIsMissingThenRejected() throws Exception {
    String requestJson = """
        {
          "card_number": "4111111111111111",
          "expiry_month": 1,
          "expiry_year": 26,
          "currency": "USD",
          "cvv": "123"
        }
        """;

    mvc.perform(MockMvcRequestBuilders.post("/payment")
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestJson))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.id").doesNotExist())
        .andExpect(jsonPath("$.status").value("Rejected"))
        .andExpect(jsonPath("$.rejectionReason").value("malformed request"));
  }

  // CVV validation tests
  @Test
  void whenCvvIsTwoCharactersThenRejected() throws Exception {
    String requestJson = """
        {
          "card_number": "4111111111111111",
          "expiry_month": 1,
          "expiry_year": 26,
          "currency": "USD",
          "amount": 1000,
          "cvv": "12"
        }
        """;

    mvc.perform(MockMvcRequestBuilders.post("/payment")
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestJson))
        .andExpect(status().isUnprocessableEntity())
        .andExpect(jsonPath("$.id").doesNotExist())
        .andExpect(jsonPath("$.status").value("Rejected"))
        .andExpect(jsonPath("$.rejectionReason").value("cvv is malformed/incorrect"));
  }

  @Test
  void whenCvvIsFiveCharactersThenRejected() throws Exception {
    String requestJson = """
        {
          "card_number": "4111111111111111",
          "expiry_month": 1,
          "expiry_year": 26,
          "currency": "USD",
          "amount": 1000,
          "cvv": "12345"
        }
        """;

    mvc.perform(MockMvcRequestBuilders.post("/payment")
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestJson))
        .andExpect(status().isUnprocessableEntity())
        .andExpect(jsonPath("$.id").doesNotExist())
        .andExpect(jsonPath("$.status").value("Rejected"))
        .andExpect(jsonPath("$.rejectionReason").value("cvv is malformed/incorrect"));
  }

  @Test
  void whenCvvHasNonNumericCharactersThenRejected() throws Exception {
    String requestJson = """
        {
          "card_number": "4111111111111111",
          "expiry_month": 1,
          "expiry_year": 26,
          "currency": "USD",
          "amount": 1000,
          "cvv": "12a"
        }
        """;

    mvc.perform(MockMvcRequestBuilders.post("/payment")
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestJson))
        .andExpect(status().isUnprocessableEntity())
        .andExpect(jsonPath("$.id").doesNotExist())
        .andExpect(jsonPath("$.status").value("Rejected"))
        .andExpect(jsonPath("$.rejectionReason").value("cvv is malformed/incorrect"));
  }

  @Test
  void whenCvvIsMissingThenRejected() throws Exception {
    String requestJson = """
        {
          "card_number": "4111111111111111",
          "expiry_month": 1,
          "expiry_year": 26,
          "currency": "USD",
          "amount": 1000
        }
        """;

    mvc.perform(MockMvcRequestBuilders.post("/payment")
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestJson))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.id").doesNotExist())
        .andExpect(jsonPath("$.status").value("Rejected"))
        .andExpect(jsonPath("$.rejectionReason").value("malformed request"));
  }

  @Test
  void whenCompletelyMalformedJsonIsSubmittedThenRejected() throws Exception {
    String requestJson = """
        {
          "randomField": "some value",
          "anotherField": 123,
          "unrelatedData": true
        }
        """;

    mvc.perform(MockMvcRequestBuilders.post("/payment")
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestJson))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.id").doesNotExist())
        .andExpect(jsonPath("$.status").value("Rejected"))
        .andExpect(jsonPath("$.rejectionReason").value("malformed request"));
  }

  // Successful processing tests
  @Test
  void whenCardNumberEndsWithEvenNumberThenDeclined() throws Exception {
    String requestJson = """
        {
          "card_number": "4111111111111112",
          "expiry_month": 1,
          "expiry_year": 26,
          "currency": "USD",
          "amount": 1000,
          "cvv": "123"
        }
        """;

    mvc.perform(MockMvcRequestBuilders.post("/payment")
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestJson))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").exists())
        .andExpect(jsonPath("$.status").value("Declined"))
        .andExpect(jsonPath("$.cardNumberLastFour").value("1112"))
        .andExpect(jsonPath("$.expiryMonth").value(1))
        .andExpect(jsonPath("$.expiryYear").value(26))
        .andExpect(jsonPath("$.currency").value("USD"))
        .andExpect(jsonPath("$.amount").value(1000));
  }

  @Test
  void whenCardNumberEndsWithZeroThenDeclined() throws Exception {
    String requestJson = """
        {
          "card_number": "4111111111111110",
          "expiry_month": 1,
          "expiry_year": 26,
          "currency": "USD",
          "amount": 1000,
          "cvv": "123"
        }
        """;

    mvc.perform(MockMvcRequestBuilders.post("/payment")
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestJson))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").exists())
        .andExpect(jsonPath("$.status").value("Declined"))
        .andExpect(jsonPath("$.cardNumberLastFour").value("1110"))
        .andExpect(jsonPath("$.expiryMonth").value(1))
        .andExpect(jsonPath("$.expiryYear").value(26))
        .andExpect(jsonPath("$.currency").value("USD"))
        .andExpect(jsonPath("$.amount").value(1000));
  }
}
