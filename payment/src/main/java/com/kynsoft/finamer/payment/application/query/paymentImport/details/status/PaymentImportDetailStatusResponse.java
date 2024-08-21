package com.kynsoft.finamer.payment.application.query.paymentImport.details.status;

import com.kynsof.share.core.domain.bus.query.IResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class PaymentImportDetailStatusResponse implements IResponse {
    private String status;
}
