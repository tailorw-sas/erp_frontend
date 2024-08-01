package com.kynsoft.finamer.payment.application.query.paymentImport.details;

import com.kynsof.share.core.domain.bus.query.IQuery;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.data.domain.Pageable;

@Getter
@AllArgsConstructor
public class PaymentImportDetailSearchErrorQuery implements IQuery {
    private Pageable pageable;
    private String importProcessId;
}
