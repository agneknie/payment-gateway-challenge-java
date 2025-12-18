package com.checkout.payment.gateway.service;

import com.checkout.payment.gateway.enums.Currency;
import com.checkout.payment.gateway.enums.PaymentStatus;
import com.checkout.payment.gateway.exception.EventProcessingException;
import com.checkout.payment.gateway.util.RejectionMessages;
import com.checkout.payment.gateway.model.BankSimulatorRequest;
import com.checkout.payment.gateway.model.PostPaymentRequest;
import com.checkout.payment.gateway.model.PostPaymentResponse;
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
    SuccessfulPaymentResponse payment = paymentsRepository.get(id)
        .orElseThrow(() -> new EventProcessingException("invalid payment ID"));

    LOG.info("Payment retrieved for ID {}", id);
    return payment;
  }

  public PostPaymentResponse processPayment(PostPaymentRequest paymentRequest) {
    LOG.info("Processing payment request");

    // Validate the request
    Set<ConstraintViolation<PostPaymentRequest>> violations = validator.validate(paymentRequest);
    if (!violations.isEmpty()) {
      LOG.info("Payment validation failed for payment request: {}", violations);
      return new RejectedPaymentResponse(PaymentStatus.REJECTED, generateRejectionReason(violations));
    }

    // Validation passed, proceed with bank call
    LOG.info("Payment validation passed");
    PaymentStatus bankStatus = sendBankRequest(paymentRequest);

    // Create payment response
    SuccessfulPaymentResponse response = createSuccessfulResponse(bankStatus, paymentRequest);

    // Store the payment
    paymentsRepository.add(response);

    LOG.info("Payment processed with status: {}", bankStatus);
    return response;
  }

  private PaymentStatus sendBankRequest(PostPaymentRequest request) {
    final BankSimulatorRequest bankRequest = new BankSimulatorRequest(
        request.getCardNumber(),
        request.getExpiryDate(),
        Currency.valueOf(request.getCurrency()),
        Integer.parseInt(request.getAmount()),
        request.getCvv()
    );
    return bankSimulatorClient.callBank(bankRequest);
  }

  private SuccessfulPaymentResponse createSuccessfulResponse(PaymentStatus status, PostPaymentRequest request) {
    UUID paymentId = UUID.randomUUID();
    String lastFour = request.getCardNumber().substring(request.getCardNumber().length() - 4);

    return new SuccessfulPaymentResponse(
        paymentId,
        status,
        lastFour,
        request.getExpiryMonth(),
        request.getExpiryYear(),
        Currency.valueOf(request.getCurrency()),
        Integer.parseInt(request.getAmount())
    );
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
