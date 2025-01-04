package com.kynsoft.finamer.payment.application.query.paymentImport.details.status;

import com.kynsof.share.core.domain.bus.query.IQuery;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class PaymentImportDetailStatusQuery implements IQuery {
    private String importProcessId;
}
