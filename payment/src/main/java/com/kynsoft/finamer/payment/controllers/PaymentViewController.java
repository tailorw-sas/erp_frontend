package com.kynsoft.finamer.payment.controllers;

import com.kynsoft.finamer.payment.domain.dtoEnum.Status;
import com.kynsoft.finamer.payment.infrastructure.identity.PaymentView;
import com.kynsoft.finamer.payment.infrastructure.services.PaymentViewService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/payments-view")
public class PaymentViewController {

    private final PaymentViewService paymentViewService;

    public PaymentViewController(PaymentViewService paymentViewService) {
        this.paymentViewService = paymentViewService;
    }

    /**
     * Obtiene todos los pagos de la vista.
     */
    @GetMapping
    public ResponseEntity<List<PaymentView>> getAllPayments() {
        List<PaymentView> payments = paymentViewService.getAllPayments();
        return ResponseEntity.ok(payments);
    }

    /**
     * Obtiene un pago específico por ID.
     */
    @GetMapping("/{id}")
    public ResponseEntity<PaymentView> getPaymentById(@PathVariable UUID id) {
        return paymentViewService.getAllPayments().stream()
                .filter(payment -> payment.getId().equals(id))
                .findFirst()
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

}
