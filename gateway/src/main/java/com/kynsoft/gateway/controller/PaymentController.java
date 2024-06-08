package com.kynsoft.gateway.controller;

import com.kynsof.share.core.application.payment.domain.placeToPlay.request.PaymentData;
import com.kynsof.share.core.application.payment.domain.placeToPlay.response.PaymentResponse;
import com.kynsof.share.core.application.payment.domain.placeToPlay.response.TransactionsState;
import com.kynsof.share.core.application.payment.domain.service.IPaymentServiceClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;

@RestController
@RequestMapping("/api/payments") // Ajusta la ruta según tus necesidades
public class PaymentController {

    private final IPaymentServiceClient paymentServiceClient;

    @Autowired
    public PaymentController(IPaymentServiceClient paymentServiceClient) {
        this.paymentServiceClient = paymentServiceClient;
    }

    @PostMapping("/make-payment")
    public ResponseEntity<PaymentResponse> makePayment(
            ServerHttpRequest request,
            @RequestHeader(value = "User-Agent", required = false, defaultValue = "Unknown") String userAgent,
            @RequestBody PaymentData paymentData) {

        String ipAddress = Objects.requireNonNull(request.getRemoteAddress()).getAddress().getHostAddress();

        paymentData.setUserAgent(userAgent);
        paymentData.setIpAddress(ipAddress);

        PaymentResponse paymentResponse = paymentServiceClient.paymentTransactions(paymentData);

        return new ResponseEntity<>(paymentResponse, HttpStatus.OK);
    }

    @GetMapping("/state/{transactionId}")
    public ResponseEntity<TransactionsState> getTransactionState(@PathVariable int transactionId) {
        TransactionsState state = paymentServiceClient.getTransactionsState(transactionId);
        return ResponseEntity.ok(state);
    }
}

