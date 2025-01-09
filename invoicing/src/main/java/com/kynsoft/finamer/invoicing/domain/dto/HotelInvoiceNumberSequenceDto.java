package com.kynsoft.finamer.invoicing.domain.dto;

import com.kynsoft.finamer.invoicing.domain.dtoEnum.EInvoiceType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class HotelInvoiceNumberSequenceDto {

    private UUID id;
    private Long invoiceNo;
    private ManageHotelDto hotel;
    private ManageTradingCompaniesDto manageTradingCompanies;
    private EInvoiceType invoiceType;

}
