package com.checkout.payment.gateway.service;

import com.checkout.payment.gateway.enums.Currency;
import com.checkout.payment.gateway.enums.PaymentStatus;
import com.checkout.payment.gateway.exception.EventProcessingException;
import com.checkout.payment.gateway.util.RejectionMessages;
import com.checkout.payment.gateway.model.BankSimulatorRequest;
import com.checkout.payment.gateway.model.PostPaymentRequest;
import com.checkout.payment.gateway.model.RejectedPaymentResponse;
import com.checkout.payment.gateway.model.SuccessfulPaymentResponse;
import com.checkout.payment.gateway.repository.PaymentsRepository;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
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
        .orElseThrow(() -> new EventProcessingException("invalid payment ID"));
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

  String generateRejectionReason(Set<ConstraintViolation<PostPaymentRequest>> violations) {
    // Check for missing fields
    boolean hasMissing = violations.stream().anyMatch(v -> RejectionMessages.MALFORMED_REQUEST.equals(v.getMessage()));
    if (hasMissing) {
      return RejectionMessages.MALFORMED_REQUEST;
    }

    // Check for expiry format errors
    boolean hasExpiryFormatError = violations.stream().anyMatch(v ->
        RejectionMessages.EXPIRY_MONTH_MALFORMED.equals(v.getMessage()) ||
        RejectionMessages.EXPIRY_YEAR_MALFORMED.equals(v.getMessage()));

    // Collect reasons, excluding EXPIRED_CARD if format errors exist
    Set<String> reasons = violations.stream()
        .map(ConstraintViolation::getMessage)
        .filter(msg -> !(hasExpiryFormatError && RejectionMessages.EXPIRED_CARD.equals(msg)))
        .collect(Collectors.toSet());

    return String.join(", ", reasons);
  }
}
