package com.kynsoft.finamer.payment.application.query.paymentImport.details;

import com.kynsof.share.core.domain.bus.query.IResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PaymentImportDetailStatusResponse implements IResponse {
    private String status;
}
