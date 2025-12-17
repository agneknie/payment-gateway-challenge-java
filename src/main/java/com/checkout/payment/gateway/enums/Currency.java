package com.checkout.payment.gateway.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum Currency {
  USD("USD"),
  GBP("GBP"),
  EUR("EUR");

  private final String name;

  Currency(String name) {
    this.name = name;
  }

  @JsonValue
  public String getName() {
    return this.name;
  }
}
