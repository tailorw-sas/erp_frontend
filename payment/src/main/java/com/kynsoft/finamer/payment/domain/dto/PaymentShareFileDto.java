package com.kynsoft.finamer.payment.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PaymentShareFileDto {
    private UUID id;
    private PaymentDto payment;
    private String fileName;
    private String fileUrl;
    private int shareFileYear;
    private int shareFileMonth;

    public PaymentShareFileDto(UUID id, PaymentDto payment, String fileName, String fileUrl) {
        this.id = id;
        this.payment = payment;
        this.fileName = fileName;
        this.fileUrl = fileUrl;
    }
}