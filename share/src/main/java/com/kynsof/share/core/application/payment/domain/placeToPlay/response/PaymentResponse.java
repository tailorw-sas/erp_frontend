package com.kynsof.share.core.application.payment.domain.placeToPlay.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor  // Constructor sin argumentos requerido por Jackson
@AllArgsConstructor // Constructor con todos los argumentos que ya tenías
public class PaymentResponse {
    private String processUrl;
    private String paymentGatewayReference;
    private String validTo;
    private String requestId;
}
