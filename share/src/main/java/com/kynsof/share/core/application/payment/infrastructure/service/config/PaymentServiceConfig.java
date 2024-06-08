package com.kynsof.share.core.application.payment.infrastructure.service.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
@Getter
@Configuration
public class PaymentServiceConfig {

    @Value("${payment.service.host}")
    private String paymentServiceBaseUrl;

    @Value("${payment.service.client-id}")
    private String paymentServiceClientId;

    @Value("${payment.service.expiration}")
    private int paymentExpirationMinutes;

    @Value("${payment.service.port}")
    private int paymentServicePort;
}