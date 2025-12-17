package com.checkout.payment.gateway.model;

import com.checkout.payment.gateway.validation.ValidCurrency;
import com.checkout.payment.gateway.validation.ValidExpiryDate;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import java.io.Serializable;

@ValidExpiryDate
@Schema(description = "Payment processing request")
public class PostPaymentRequest implements Serializable {

  @JsonProperty("card_number")
  @NotBlank(message = "Card number is required")
  @Pattern(regexp = "^\\d{14,19}$", message = "Card number must be 14-19 digits")
  private String cardNumber;

  @JsonProperty("expiry_month")
  @NotNull(message = "Expiry month is required")
  @Min(value = 1, message = "Expiry month must be between 1-12")
  @Max(value = 12, message = "Expiry month must be between 1-12")
  private Integer expiryMonth;

  @JsonProperty("expiry_year")
  @NotNull(message = "Expiry year is required")
  @Max(value = 99, message = "Expiry year must be 2-digit (00-99)")
  private Integer expiryYear;

  @NotBlank(message = "Currency is required")
  @ValidCurrency
  private String currency;

  @NotBlank(message = "Amount is required")
  @Pattern(regexp = "^\\d+$", message = "Amount must be a positive integer")
  private String amount;

  @NotBlank(message = "CVV is required")
  @Pattern(regexp = "^\\d{3,4}$", message = "CVV must be 3-4 digits")
  private String cvv;

  public String getCardNumber() {
    return cardNumber;
  }

  public void setCardNumber(String cardNumber) {
    this.cardNumber = cardNumber;
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

  public String getCurrency() {
    return currency;
  }

  public void setCurrency(String currency) {
    this.currency = currency;
  }

  public String getAmount() {
    return amount;
  }

  public void setAmount(String amount) {
    this.amount = amount;
  }

  public String getCvv() {
    return cvv;
  }

  public void setCvv(String cvv) {
    this.cvv = cvv;
  }

  @Schema(hidden = true)
  public String getExpiryDate() {
    return String.format("%02d/%02d", expiryMonth, expiryYear);
  }

  @Override
  public String toString() {
    return "PostPaymentRequest{" +
        "cardNumber='" + cardNumber + '\'' +
        ", expiryMonth=" + expiryMonth +
        ", expiryYear=" + expiryYear +
        ", currency='" + currency + '\'' +
        ", amount=" + amount +
        ", cvv='" + cvv + '\'' +
        '}';
  }
}
