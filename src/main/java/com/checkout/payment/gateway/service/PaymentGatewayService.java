package com.checkout.payment.gateway.service;

import com.checkout.payment.gateway.enums.Currency;
import com.checkout.payment.gateway.enums.PaymentStatus;
import com.checkout.payment.gateway.exception.EventProcessingException;
import com.checkout.payment.gateway.model.BankSimulatorRequest;
import com.checkout.payment.gateway.model.PostPaymentRequest;
import com.checkout.payment.gateway.model.RejectedPaymentResponse;
import com.checkout.payment.gateway.model.SuccessfulPaymentResponse;
import com.checkout.payment.gateway.repository.PaymentsRepository;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import java.util.Set;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class PaymentGatewayService {

  private static final Logger LOG = LoggerFactory.getLogger(PaymentGatewayService.class);

  private final PaymentsRepository paymentsRepository;
  private final BankSimulatorClient bankSimulatorClient;
  private final Validator validator;

  public PaymentGatewayService(PaymentsRepository paymentsRepository, BankSimulatorClient bankSimulatorClient, Validator validator) {
    this.paymentsRepository = paymentsRepository;
    this.bankSimulatorClient = bankSimulatorClient;
    this.validator = validator;
  }

  public SuccessfulPaymentResponse getPaymentById(UUID id) {
    LOG.debug("Requesting access to payment with ID {}", id);
    SuccessfulPaymentResponse payment = paymentsRepository.get(id)
        .orElseThrow(() -> new EventProcessingException("Invalid payment ID"));
    LOG.info("Payment retrieved for ID {}", id);
    return payment;
  }

  public Object processPayment(PostPaymentRequest paymentRequest) {
    LOG.info("Processing payment request");

    // Validate the request
    Set<ConstraintViolation<PostPaymentRequest>> violations = validator.validate(paymentRequest);
    if (!violations.isEmpty()) {
      LOG.info("Validation failed for payment request: {}", violations);
      // Return REJECTED response (not stored)
      String rejectionReason = generateRejectionReason(violations);
      return new RejectedPaymentResponse(PaymentStatus.REJECTED, rejectionReason);
    }

    // Validation passed, proceed with bank call
    LOG.debug("Validation passed, calling bank simulator for card ending in: {}",
             paymentRequest.getCardNumber().substring(paymentRequest.getCardNumber().length() - 4));

    UUID paymentId = UUID.randomUUID();

    // Call bank simulator
    BankSimulatorRequest bankRequest = new BankSimulatorRequest(
        paymentRequest.getCardNumber(),
        paymentRequest.getExpiryDate(),
        Currency.valueOf(paymentRequest.getCurrency()),
        Integer.parseInt(paymentRequest.getAmount()),
        paymentRequest.getCvv()
    );

    PaymentStatus bankStatus = bankSimulatorClient.callBank(bankRequest);

    // Set last 4 digits of card
    String cardNumber = paymentRequest.getCardNumber();
    String lastFour = cardNumber.substring(cardNumber.length() - 4);

    // Create successful response
    SuccessfulPaymentResponse successfulResponse = new SuccessfulPaymentResponse(
        paymentId,
        bankStatus,
        lastFour,
        paymentRequest.getExpiryMonth(),
        paymentRequest.getExpiryYear(),
        Currency.valueOf(paymentRequest.getCurrency()),
        Integer.parseInt(paymentRequest.getAmount())
    );

    // Store the payment
    paymentsRepository.add(successfulResponse);

    LOG.info("Payment processed with status: {}", bankStatus);
    return successfulResponse;
  }

  private String generateRejectionReason(Set<ConstraintViolation<PostPaymentRequest>> violations) {
    boolean hasMissingFields = false;

    // Check if any violations are for missing fields
    for (ConstraintViolation<PostPaymentRequest> violation : violations) {
      String message = violation.getMessage();
      if (message.contains("is required") || message.contains("must not be blank")) {
        hasMissingFields = true;
        break;
      }
    }

    // If any fields are missing, return generic message
    if (hasMissingFields) {
      return "malformed request";
    }

    // Otherwise, provide detailed reasons for malformed present fields
    Set<String> reasons = new java.util.HashSet<>();
    boolean hasExpiryFieldFormatErrors = false;

    // First pass: check for expiry field format errors
    for (ConstraintViolation<PostPaymentRequest> violation : violations) {
      String message = violation.getMessage();
      String property = violation.getPropertyPath().toString();

      if ((property.equals("expiryMonth") || property.equals("expiryYear")) &&
          (message.contains("must be between 1-12") || message.contains("must be 2-digit"))) {
        hasExpiryFieldFormatErrors = true;
        break;
      }
    }

    // Second pass: add reasons
    for (ConstraintViolation<PostPaymentRequest> violation : violations) {
      String message = violation.getMessage();
      String property = violation.getPropertyPath().toString();

      if (violation.getConstraintDescriptor().getAnnotation() instanceof com.checkout.payment.gateway.validation.ValidExpiryDate) {
        // Only add "Expired card" if no expiry field format errors
        if (!hasExpiryFieldFormatErrors) {
          reasons.add("Expired card");
        }
      } else if (message.contains("must be between 1-12") || message.contains("must be 2-digit") ||
                 message.contains("must be 14-19 digits") || message.contains("must be 3-4 digits") ||
                 message.contains("must be a positive integer")) {
        reasons.add(property + " is malformed/incorrect");
      } else if (message.contains("Currency is not supported")) {
        reasons.add("Currency is not supported");
      }
    }

    return String.join(", ", reasons);
  }
}
