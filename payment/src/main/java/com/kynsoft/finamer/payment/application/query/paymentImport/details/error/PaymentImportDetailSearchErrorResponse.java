package com.kynsoft.finamer.payment.application.query.paymentImport.details.error;

import com.kynsof.share.core.domain.bus.query.IResponse;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class PaymentImportDetailSearchErrorResponse implements IResponse {
    private PaginatedResponse paginatedResponse;
}
