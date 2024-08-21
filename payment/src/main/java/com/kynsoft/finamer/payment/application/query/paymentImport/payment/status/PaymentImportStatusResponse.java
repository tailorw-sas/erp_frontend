package com.kynsoft.finamer.payment.application.query.paymentImport.payment.status;

import com.kynsof.share.core.domain.bus.query.IResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class PaymentImportStatusResponse implements IResponse {
    private String status;
}
