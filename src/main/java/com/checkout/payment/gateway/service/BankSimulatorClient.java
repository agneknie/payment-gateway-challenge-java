package com.checkout.payment.gateway.service;

import com.checkout.payment.gateway.enums.PaymentStatus;
import com.checkout.payment.gateway.model.BankSimulatorRequest;
import com.checkout.payment.gateway.model.BankSimulatorResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Service
public class BankSimulatorClient {

  private static final Logger LOG = LoggerFactory.getLogger(BankSimulatorClient.class);
  private static final String BANK_SIMULATOR_URL = "http://localhost:8080/payments";

  private final RestTemplate restTemplate;

  public BankSimulatorClient(RestTemplate restTemplate) {
    this.restTemplate = restTemplate;
  }

  public PaymentStatus callBank(BankSimulatorRequest request) {
    try {
      LOG.debug("Calling bank simulator with card ending in: {}", request.getCard_number().substring(request.getCard_number().length() - 1));

      ResponseEntity<BankSimulatorResponse> response = restTemplate.postForEntity(
          BANK_SIMULATOR_URL,
          request,
          BankSimulatorResponse.class
      );

      if (response.getStatusCode() == HttpStatus.OK) {
        BankSimulatorResponse body = response.getBody();
        if (body != null) {
          return body.isAuthorized() ? PaymentStatus.AUTHORIZED : PaymentStatus.DECLINED;
        }
      } else if (response.getStatusCode() == HttpStatus.BAD_REQUEST) {
        LOG.warn("Bank simulator returned 400 Bad Request - invalid request format");
        return PaymentStatus.DECLINED;
      } else if (response.getStatusCode() == HttpStatus.SERVICE_UNAVAILABLE) {
        LOG.info("Bank simulator returned 503 Service Unavailable");
        return PaymentStatus.DECLINED;
      }

      LOG.error("Unexpected response from bank simulator: {}", response.getStatusCode());
      return PaymentStatus.DECLINED;

    } catch (HttpServerErrorException e) {
      if (e.getStatusCode() == HttpStatus.SERVICE_UNAVAILABLE) {
        LOG.info("Bank simulator returned 503 Service Unavailable");
        return PaymentStatus.DECLINED;
      } else {
        LOG.warn("Bank simulator returned server error: {}", e.getStatusCode());
        return PaymentStatus.DECLINED;
      }
    } catch (RestClientException e) {
      LOG.error("Failed to call bank simulator", e);
      return PaymentStatus.DECLINED;
    }
  }
}
