package com.kynsoft.finamer.invoicing.application.command.hotelInvoiceNumberSequence.updateByCode;

import com.kynsoft.finamer.invoicing.domain.dtoEnum.EInvoiceType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateCodeAndValue {

    private String code;
    private EInvoiceType invoiceType;
    private Long value;
}
