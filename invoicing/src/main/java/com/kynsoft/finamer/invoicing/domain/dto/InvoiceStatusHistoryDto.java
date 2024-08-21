package com.kynsoft.finamer.invoicing.domain.dto;

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
public class InvoiceStatusHistoryDto {

    private UUID id;
    private ManageInvoiceDto invoice;
    private String description;
    private LocalDateTime createdAt;
    private String employee;
    private EInvoiceStatus invoiceStatus;

}
