package com.checkout.payment.gateway.model;

import com.checkout.payment.gateway.enums.Currency;
import com.checkout.payment.gateway.enums.PaymentStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.UUID;

@Schema(description = "Successful payment processing response")
public class SuccessfulPaymentResponse {
  private UUID id;
  private PaymentStatus status;
  private String cardNumberLastFour;
  private Integer expiryMonth;
  private Integer expiryYear;
  private Currency currency;
  private Integer amount;

  public SuccessfulPaymentResponse() {}

  public SuccessfulPaymentResponse(UUID id, PaymentStatus status, String cardNumberLastFour,
                                   Integer expiryMonth, Integer expiryYear, Currency currency, Integer amount) {
    this.id = id;
    this.status = status;
    this.cardNumberLastFour = cardNumberLastFour;
    this.expiryMonth = expiryMonth;
    this.expiryYear = expiryYear;
    this.currency = currency;
    this.amount = amount;
  }

  public UUID getId() {
    return id;
  }

  public void setId(UUID id) {
    this.id = id;
  }

  public PaymentStatus getStatus() {
    return status;
  }

  public void setStatus(PaymentStatus status) {
    this.status = status;
  }

  public String getCardNumberLastFour() {
    return cardNumberLastFour;
  }

  public void setCardNumberLastFour(String cardNumberLastFour) {
    this.cardNumberLastFour = cardNumberLastFour;
  }

  public Integer getExpiryMonth() {
    return expiryMonth;
  }

  public void setExpiryMonth(Integer expiryMonth) {
    this.expiryMonth = expiryMonth;
  }

  public Integer getExpiryYear() {
    return expiryYear;
  }

  public void setExpiryYear(Integer expiryYear) {
    this.expiryYear = expiryYear;
  }

  public Currency getCurrency() {
    return currency;
  }

  public void setCurrency(Currency currency) {
    this.currency = currency;
  }

  public Integer getAmount() {
    return amount;
  }

  public void setAmount(Integer amount) {
    this.amount = amount;
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
