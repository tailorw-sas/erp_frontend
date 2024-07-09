package com.kynsoft.finamer.invoicing.application.query.objectResponse;

import com.kynsof.share.core.domain.bus.query.IResponse;
import com.kynsoft.finamer.invoicing.domain.dto.ManageAdjustmentDto;
import com.kynsoft.finamer.invoicing.domain.dto.ManageInvoiceTransactionTypeDto;
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
    private Long adjustment_id;

    private LocalDateTime date;
    private String description;
    private ManageInvoiceTransactionTypeDto transaction;
    private ManageRoomRateDto roomRate;
    private String employee;

    public ManageAdjustmentResponse(ManageAdjustmentDto dto) {
        this.id = dto.getId();
        this.amount = dto.getAmount();
        this.date = dto.getDate();
        this.description = dto.getDescription();
        this.transaction = dto.getTransaction();
        this.roomRate = dto.getRoomRate();
        this.adjustment_id = dto.getAdjustment_id();
        this.employee = dto.getEmployee();
    }
}
