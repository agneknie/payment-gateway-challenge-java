package com.checkout.payment.gateway.model;

import com.checkout.payment.gateway.enums.Currency;
import com.checkout.payment.gateway.enums.PaymentStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.UUID;

@Schema(description = "Successful payment processing response")
public class SuccessfulPaymentResponse extends PostPaymentResponse {
  private UUID id;
  private String cardNumberLastFour;
  private Integer expiryMonth;
  private Integer expiryYear;
  private Currency currency;
  private Integer amount;

  public SuccessfulPaymentResponse(UUID id, PaymentStatus status, String cardNumberLastFour,
                                   Integer expiryMonth, Integer expiryYear, Currency currency, Integer amount) {
    super(status);
    this.id = id;
    this.cardNumberLastFour = cardNumberLastFour;
    this.expiryMonth = expiryMonth;
    this.expiryYear = expiryYear;
    this.currency = currency;
    this.amount = amount;
  }

  public UUID getId() {
    return id;
  }

  public String getCardNumberLastFour() {
    return cardNumberLastFour;
  }

  public Integer getExpiryMonth() {
    return expiryMonth;
  }

  public Integer getExpiryYear() {
    return expiryYear;
  }

  public Currency getCurrency() {
    return currency;
  }

  public Integer getAmount() {
    return amount;
  }

  @Override
  public String toString() {
    return "SuccessfulPaymentResponse{" +
        "id=" + id +
        ", status=" + status +
        ", cardNumberLastFour='" + cardNumberLastFour + '\'' +
        ", expiryMonth=" + expiryMonth +
        ", expiryYear=" + expiryYear +
        ", currency='" + currency + '\'' +
        ", amount=" + amount +
        '}';
  }
}
