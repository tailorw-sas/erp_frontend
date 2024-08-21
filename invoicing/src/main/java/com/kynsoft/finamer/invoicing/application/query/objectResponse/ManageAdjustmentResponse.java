package com.kynsoft.finamer.invoicing.application.query.objectResponse;

import com.kynsof.share.core.domain.bus.query.IResponse;
import com.kynsoft.finamer.invoicing.domain.dto.ManageAdjustmentDto;
import com.kynsoft.finamer.invoicing.domain.dto.ManageInvoiceTransactionTypeDto;
import com.kynsoft.finamer.invoicing.domain.dto.ManagePaymentTransactionTypeDto;
import com.kynsoft.finamer.invoicing.domain.dto.ManageRoomRateDto;
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
public class ManageAdjustmentResponse implements IResponse {
    private UUID id;
    private Double amount;
    private Long adjustmentId;

    private LocalDateTime date;
    private String description;
    private ManageInvoiceTransactionTypeDto transaction;
    private ManagePaymentTransactionTypeDto paymentTransactionType;
    private ManageRoomRateDto roomRate;
    private String employee;

    public ManageAdjustmentResponse(ManageAdjustmentDto dto) {
        this.id = dto.getId();
        this.amount = dto.getAmount();
        this.date = dto.getDate();
        this.description = dto.getDescription();
        this.transaction = dto.getTransaction();
        this.roomRate = dto.getRoomRate();
        this.adjustmentId = dto.getAdjustmentId();
        this.employee = dto.getEmployee();
        this.paymentTransactionType = dto.getPaymentTransactionType();
    }
}
