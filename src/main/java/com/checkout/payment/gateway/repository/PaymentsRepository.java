package com.checkout.payment.gateway.repository;

import com.checkout.payment.gateway.model.SuccessfulPaymentResponse;
import java.util.HashMap;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Repository;

@Repository
public class PaymentsRepository {

  private final HashMap<UUID, SuccessfulPaymentResponse> payments = new HashMap<>();

  public void add(SuccessfulPaymentResponse payment) {
    payments.put(payment.getId(), payment);
  }

  public Optional<SuccessfulPaymentResponse> get(UUID id) {
    return Optional.ofNullable(payments.get(id));
  }

  public int size() {
    return payments.size();
  }

}
