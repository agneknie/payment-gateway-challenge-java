package com.checkout.payment.gateway.model;

import com.checkout.payment.gateway.enums.PaymentStatus;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Rejected payment response")
public class RejectedPaymentResponse extends PostPaymentResponse {
  private String rejectionReason;

  public RejectedPaymentResponse(PaymentStatus status, String rejectionReason) {
    super(status);
    this.rejectionReason = rejectionReason;
  }

  public String getRejectionReason() {
    return rejectionReason;
  }

  @Override
  public String toString() {
    return "RejectedPaymentResponse{" +
        "status=" + status +
        ", rejectionReason='" + rejectionReason + '\'' +
        '}';
  }
}
