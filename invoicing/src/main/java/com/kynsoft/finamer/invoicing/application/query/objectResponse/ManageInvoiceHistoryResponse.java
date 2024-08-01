package com.kynsoft.finamer.invoicing.application.query.objectResponse;

import com.kynsoft.finamer.invoicing.domain.dto.ManageInvoiceDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ManageInvoiceHistoryResponse {

    private UUID id;
    private Long invoiceId;
    private Long invoiceNo;

    public ManageInvoiceHistoryResponse(ManageInvoiceDto dto){
        this.id = dto.getId();
        this.invoiceId = dto.getInvoiceId();
        this.invoiceNo = dto.getInvoiceNo();
    }
}
