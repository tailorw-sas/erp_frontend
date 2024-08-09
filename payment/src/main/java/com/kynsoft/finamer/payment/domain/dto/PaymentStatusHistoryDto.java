package com.kynsoft.finamer.payment.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class PaymentStatusHistoryDto {

    private UUID id;
    private Long paymentHistoryId;
    private String status;
    private PaymentDto payment;
    private ManageEmployeeDto employee;
    private String description;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

}
