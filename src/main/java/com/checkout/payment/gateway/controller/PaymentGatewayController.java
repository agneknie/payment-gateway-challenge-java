package com.checkout.payment.gateway.controller;

import com.checkout.payment.gateway.model.PostPaymentRequest;
import com.checkout.payment.gateway.model.RejectedPaymentResponse;
import com.checkout.payment.gateway.model.SuccessfulPaymentResponse;
import com.checkout.payment.gateway.service.PaymentGatewayService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "Payment Gateway", description = "API for processing and retrieving payments")
public class PaymentGatewayController {

  private final PaymentGatewayService paymentGatewayService;

  public PaymentGatewayController(PaymentGatewayService paymentGatewayService) {
    this.paymentGatewayService = paymentGatewayService;
  }

  @PostMapping("/payment")
  @Operation(summary = "Process a payment", description = "Submit a payment request for processing through the payment gateway")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Payment processed successfully",
          content = @Content(mediaType = "application/json", schema = @Schema(implementation = SuccessfulPaymentResponse.class))),
      @ApiResponse(responseCode = "400", description = "Malformed request - missing required fields",
          content = @Content(mediaType = "application/json", schema = @Schema(implementation = RejectedPaymentResponse.class))),
      @ApiResponse(responseCode = "422", description = "Payment rejected due to validation errors",
          content = @Content(mediaType = "application/json", schema = @Schema(implementation = RejectedPaymentResponse.class)))
  })
  public ResponseEntity<?> processPayment(@RequestBody PostPaymentRequest request) {
    Object response = paymentGatewayService.processPayment(request);
    if (response instanceof SuccessfulPaymentResponse) {
      return new ResponseEntity<>(response, HttpStatus.OK);
    } else if (response instanceof RejectedPaymentResponse) {
      RejectedPaymentResponse rejected = (RejectedPaymentResponse) response;
      HttpStatus status = "malformed request".equals(rejected.getRejectionReason())
          ? HttpStatus.BAD_REQUEST
          : HttpStatus.UNPROCESSABLE_ENTITY;
      return new ResponseEntity<>(response, status);
    } else {
      return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  @GetMapping("/payment/{id}")
  @Operation(summary = "Retrieve payment details", description = "Get details of a previously processed payment by ID")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Payment found",
          content = @Content(mediaType = "application/json", schema = @Schema(implementation = SuccessfulPaymentResponse.class))),
      @ApiResponse(responseCode = "404", description = "Payment not found",
          content = @Content(mediaType = "application/json", schema = @Schema(implementation = com.checkout.payment.gateway.model.ErrorResponse.class)))
  })
  public ResponseEntity<SuccessfulPaymentResponse> getPaymentById(@PathVariable UUID id) {
    SuccessfulPaymentResponse payment = paymentGatewayService.getPaymentById(id);
    return new ResponseEntity<>(payment, HttpStatus.OK);
  }
}
