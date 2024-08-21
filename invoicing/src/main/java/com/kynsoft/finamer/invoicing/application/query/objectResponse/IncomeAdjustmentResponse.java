package com.kynsoft.finamer.invoicing.application.query.objectResponse;

import com.kynsof.share.core.domain.bus.query.IResponse;
import com.kynsoft.finamer.invoicing.domain.dto.IncomeAdjustmentDto;
import com.kynsoft.finamer.invoicing.domain.dtoEnum.Status;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class IncomeAdjustmentResponse implements IResponse {

    private UUID id;
    private Status status;
    private IncomeResponse income;
    private ManageInvoiceTransactionTypeResponse transactionType;
    private ManagePaymentTransactionTypeResponse paymentTransactionType;
    private Double amount;
    private LocalDate date;
    private String remark;

    public IncomeAdjustmentResponse(IncomeAdjustmentDto dto) {
        this.id = dto.getId();
        this.status = dto.getStatus();
        this.income = dto.getIncome() != null ? new IncomeResponse(dto.getIncome()) : null;
        this.transactionType = dto.getTransactionType() != null ? new ManageInvoiceTransactionTypeResponse(dto.getTransactionType()) : null;
        this.paymentTransactionType = new ManagePaymentTransactionTypeResponse(dto.getPaymentTransactionType());
        this.amount = dto.getAmount();
        this.date = dto.getDate();
        this.remark = dto.getRemark();
    }

}
