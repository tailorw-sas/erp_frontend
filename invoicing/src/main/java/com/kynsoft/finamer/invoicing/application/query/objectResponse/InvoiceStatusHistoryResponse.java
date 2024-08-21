package com.kynsoft.finamer.invoicing.application.query.objectResponse;

import com.kynsof.share.core.domain.bus.query.IResponse;
import com.kynsoft.finamer.invoicing.domain.dto.InvoiceStatusHistoryDto;
import com.kynsoft.finamer.invoicing.domain.dto.ManageInvoiceDto;
import com.kynsoft.finamer.invoicing.domain.dtoEnum.EInvoiceStatus;
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
public class InvoiceStatusHistoryResponse implements IResponse {

    private UUID id;
    private ManageInvoiceDto invoice;
    private String description;
    private String employee;
    private LocalDateTime createdAt;
    private EInvoiceStatus invoiceStatus;

    public InvoiceStatusHistoryResponse(InvoiceStatusHistoryDto dto){
        this.id = dto.getId();
        this.invoice = dto.getInvoice();
        this.description = dto.getDescription();
        this.createdAt = dto.getCreatedAt();
        this.employee = dto.getEmployee();
        this.invoiceStatus = dto.getInvoiceStatus();
    }
}
