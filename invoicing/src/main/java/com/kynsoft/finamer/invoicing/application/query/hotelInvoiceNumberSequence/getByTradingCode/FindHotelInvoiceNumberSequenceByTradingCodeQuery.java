package com.kynsoft.finamer.invoicing.application.query.hotelInvoiceNumberSequence.getByTradingCode;

import com.kynsof.share.core.domain.bus.query.IQuery;
import com.kynsoft.finamer.invoicing.domain.dtoEnum.EInvoiceType;
import lombok.Getter;

@Getter
public class FindHotelInvoiceNumberSequenceByTradingCodeQuery  implements IQuery {

    private final String code;
    private final EInvoiceType invoiceType;

    public FindHotelInvoiceNumberSequenceByTradingCodeQuery(String code, EInvoiceType invoiceType) {
        this.code = code;
        this.invoiceType = invoiceType;
    }

}
