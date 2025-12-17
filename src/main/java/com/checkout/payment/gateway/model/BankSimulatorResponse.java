package com.checkout.payment.gateway.model;

public class BankSimulatorResponse {
  private boolean authorized;
  private String authorization_code;

  public BankSimulatorResponse() {}

  public BankSimulatorResponse(boolean authorized, String authorizationCode) {
    this.authorized = authorized;
    this.authorization_code = authorizationCode;
  }

  public boolean isAuthorized() {
    return authorized;
  }

  public void setAuthorized(boolean authorized) {
    this.authorized = authorized;
  }

  public String getAuthorization_code() {
    return authorization_code;
  }

  public void setAuthorization_code(String authorization_code) {
    this.authorization_code = authorization_code;
  }
}
