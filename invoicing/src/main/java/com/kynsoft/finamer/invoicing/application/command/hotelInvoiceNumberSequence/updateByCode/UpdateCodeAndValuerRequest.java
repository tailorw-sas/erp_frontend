package com.kynsoft.finamer.invoicing.application.command.hotelInvoiceNumberSequence.updateByCode;

import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateCodeAndValuerRequest {

    private List<UpdateCodeAndValue> hotels;
    private List<UpdateCodeAndValue> tradings;
}
