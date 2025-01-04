package com.kynsoft.finamer.payment.domain.services;

import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsoft.finamer.payment.application.command.paymentImport.detail.PaymentImportDetailRequest;
import com.kynsoft.finamer.payment.application.query.paymentImport.details.error.PaymentImportDetailSearchErrorQuery;
import com.kynsoft.finamer.payment.application.query.paymentImport.details.status.PaymentImportDetailStatusQuery;
import com.kynsoft.finamer.payment.application.query.paymentImport.details.status.PaymentImportDetailStatusResponse;

public interface IPaymentImportDetailService {
   void importPaymentFromFile(PaymentImportDetailRequest request);
    PaymentImportDetailStatusResponse getPaymentImportStatus(PaymentImportDetailStatusQuery query);

    PaginatedResponse getPaymentImportErrors(PaymentImportDetailSearchErrorQuery query);
}
