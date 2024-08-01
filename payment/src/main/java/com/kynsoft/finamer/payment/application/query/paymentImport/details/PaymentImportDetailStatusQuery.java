package com.kynsoft.finamer.payment.application.query.paymentImport.details;

import com.kynsof.share.core.domain.bus.query.IQuery;
import lombok.Getter;

@Getter
public class PaymentImportDetailStatusQuery implements IQuery {
    private String importProcessId;
}
