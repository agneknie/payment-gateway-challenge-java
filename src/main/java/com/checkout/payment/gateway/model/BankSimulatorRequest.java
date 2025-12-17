package com.checkout.payment.gateway.model;

import com.checkout.payment.gateway.enums.Currency;

public class BankSimulatorRequest {
  private String card_number;
  private String expiry_date;
  private Currency currency;
  private int amount;
  private String cvv;

  public BankSimulatorRequest() {}

  public BankSimulatorRequest(String cardNumber, String expiryDate, Currency currency, int amount, String cvv) {
    this.card_number = cardNumber;
    this.expiry_date = expiryDate;
    this.currency = currency;
    this.amount = amount;
    this.cvv = cvv;
  }

  public String getCard_number() {
    return card_number;
  }

  public void setCard_number(String card_number) {
    this.card_number = card_number;
  }

  public String getExpiry_date() {
    return expiry_date;
  }

  public void setExpiry_date(String expiry_date) {
    this.expiry_date = expiry_date;
  }

  public Currency getCurrency() {
    return currency;
  }

  public void setCurrency(Currency currency) {
    this.currency = currency;
  }

  public int getAmount() {
    return amount;
  }

  public void setAmount(int amount) {
    this.amount = amount;
  }

  public String getCvv() {
    return cvv;
  }

  public void setCvv(String cvv) {
    this.cvv = cvv;
  }
}
