package com.kynsoft.finamer.payment.application.query.paymentImport.details.error;

import com.kynsof.share.core.domain.bus.query.IQuery;
import com.kynsof.share.core.domain.request.SearchRequest;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PaymentImportDetailSearchErrorQuery implements IQuery {
    private SearchRequest searchRequest;
}
