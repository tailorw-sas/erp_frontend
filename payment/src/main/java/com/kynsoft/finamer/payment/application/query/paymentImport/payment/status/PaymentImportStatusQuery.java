package com.kynsoft.finamer.payment.application.query.paymentImport.payment.status;

import com.kynsof.share.core.domain.bus.query.IQuery;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class PaymentImportStatusQuery implements IQuery {
    private String importProcessId;
}
