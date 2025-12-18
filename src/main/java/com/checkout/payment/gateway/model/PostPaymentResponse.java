package com.checkout.payment.gateway.model;

import com.checkout.payment.gateway.enums.PaymentStatus;

public abstract class PostPaymentResponse {
  protected PaymentStatus status;

  public PostPaymentResponse(PaymentStatus status) {
    this.status = status;
  }

  public PaymentStatus getStatus() {
    return status;
  }
}
