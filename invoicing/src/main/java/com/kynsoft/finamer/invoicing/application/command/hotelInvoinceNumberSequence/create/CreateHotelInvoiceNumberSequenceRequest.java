package com.kynsoft.finamer.invoicing.application.command.hotelInvoinceNumberSequence.create;

import com.kynsoft.finamer.invoicing.domain.dtoEnum.EInvoiceType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateHotelInvoiceNumberSequenceRequest {

    private String codeHotel;
    private String codeTradingCompanies;
    private EInvoiceType invoiceType;
    private Long invoiceNo;
}
