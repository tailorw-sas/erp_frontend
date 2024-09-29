package com.kynsoft.finamer.invoicing.domain.dto;

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
public class ManageAdjustmentDto {
    private UUID id;

    private Long adjustmentId;
    private Double amount;
    private LocalDateTime date;
    private String description;
    private ManageInvoiceTransactionTypeDto transaction;
    private ManagePaymentTransactionTypeDto paymentTransactionType;
    private ManageRoomRateDto roomRate;
    private String employee;

    public ManageAdjustmentDto(ManageAdjustmentDto dto) {
        this.id = UUID.randomUUID();
        this.adjustmentId = dto.getAdjustmentId();
        this.amount = dto.getAmount();
        this.date = dto.getDate();
        this.description = dto.getDescription();
        this.transaction = dto.getTransaction();
        this.paymentTransactionType = dto.getPaymentTransactionType();
        this.roomRate = dto.getRoomRate();
        this.employee = dto.getEmployee();
    }
}
