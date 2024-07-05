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

    private Long adjustment_id;
    private Double amount;
    private LocalDateTime date;
    private String description;
    private ManageInvoiceTransactionTypeDto transaction;
    private ManageRoomRateDto roomRate;
}
