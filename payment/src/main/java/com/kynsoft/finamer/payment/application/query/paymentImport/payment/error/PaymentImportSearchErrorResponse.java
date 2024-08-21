package com.kynsoft.finamer.payment.application.query.paymentImport.payment.error;

import com.kynsof.share.core.domain.bus.query.IResponse;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class PaymentImportSearchErrorResponse implements IResponse {
    private PaginatedResponse paginatedResponse;
}
