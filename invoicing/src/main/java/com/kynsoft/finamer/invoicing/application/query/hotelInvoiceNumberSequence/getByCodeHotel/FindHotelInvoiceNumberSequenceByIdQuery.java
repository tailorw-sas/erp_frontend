package com.kynsoft.finamer.invoicing.application.query.hotelInvoiceNumberSequence.getByCodeHotel;

import com.kynsof.share.core.domain.bus.query.IQuery;
import com.kynsoft.finamer.invoicing.domain.dtoEnum.EInvoiceType;
import lombok.Getter;

@Getter
public class FindHotelInvoiceNumberSequenceByIdQuery  implements IQuery {

    private final String code;
    private final EInvoiceType invoiceType;

    public FindHotelInvoiceNumberSequenceByIdQuery(String code, EInvoiceType invoiceType) {
        this.code = code;
        this.invoiceType = invoiceType;
    }

}
