package com.kynsoft.finamer.invoicing.domain.rules.manageInvoice;

import com.kynsof.share.core.domain.exception.DomainErrorMessage;
import com.kynsof.share.core.domain.response.ErrorField;
import com.kynsof.share.core.domain.rules.BusinessRule;
import com.kynsoft.finamer.invoicing.domain.dto.ManageHotelDto;

public class InvoiceManualValidateVirtualHotelRule extends BusinessRule {

    private final ManageHotelDto hotelDto;

    public InvoiceManualValidateVirtualHotelRule(ManageHotelDto hotelDto) {
        super(
                DomainErrorMessage.MANUAL_INVOICE_NOT_VIRTUAL_HOTEL, 
                new ErrorField("hotel", DomainErrorMessage.MANUAL_INVOICE_NOT_VIRTUAL_HOTEL.getReasonPhrase())
        );
        this.hotelDto = hotelDto;
    }

    @Override
    public boolean isBroken() {
        return this.hotelDto.isVirtual();
    }

}
