package com.checkout.payment.gateway.model;

import com.checkout.payment.gateway.enums.PaymentStatus;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Rejected payment response")
public class RejectedPaymentResponse {
  private PaymentStatus status;
  private String rejectionReason;

  public RejectedPaymentResponse() {}

  public RejectedPaymentResponse(PaymentStatus status, String rejectionReason) {
    this.status = status;
    this.rejectionReason = rejectionReason;
  }

  public PaymentStatus getStatus() {
    return status;
  }

  public void setStatus(PaymentStatus status) {
    this.status = status;
  }

  public String getRejectionReason() {
    return rejectionReason;
  }

  public void setRejectionReason(String rejectionReason) {
    this.rejectionReason = rejectionReason;
  }

  @Override
  public String toString() {
    return "RejectedPaymentResponse{" +
        "status=" + status +
        ", rejectionReason='" + rejectionReason + '\'' +
        '}';
  }
}
