package com.checkout.payment.gateway.model;

import com.checkout.payment.gateway.util.RejectionMessages;
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
  @NotBlank(message = RejectionMessages.MALFORMED_REQUEST)
  @Pattern(regexp = "^\\d{14,19}$", message = RejectionMessages.CARD_NUMBER_MALFORMED)
  private String cardNumber;

  @JsonProperty("expiry_month")
  @NotNull(message = RejectionMessages.MALFORMED_REQUEST)
  @Min(value = 1, message = RejectionMessages.EXPIRY_MONTH_MALFORMED)
  @Max(value = 12, message = RejectionMessages.EXPIRY_MONTH_MALFORMED)
  private Integer expiryMonth;

  @JsonProperty("expiry_year")
  @NotNull(message = RejectionMessages.MALFORMED_REQUEST)
  @Min(value = 1, message = RejectionMessages.EXPIRY_YEAR_MALFORMED)
  @Max(value = 99, message = RejectionMessages.EXPIRY_YEAR_MALFORMED)
  private Integer expiryYear;

  @NotBlank(message = RejectionMessages.MALFORMED_REQUEST)
  @ValidCurrency
  private String currency;

  @NotBlank(message = RejectionMessages.MALFORMED_REQUEST)
  @Pattern(regexp = "^\\d+$", message = RejectionMessages.AMOUNT_MALFORMED)
  private String amount;

  @NotBlank(message = RejectionMessages.MALFORMED_REQUEST)
  @Pattern(regexp = "^\\d{3,4}$", message = RejectionMessages.CVV_MALFORMED)
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
