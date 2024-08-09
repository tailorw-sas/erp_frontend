package com.kynsoft.finamer.payment.domain.services;

import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsoft.finamer.payment.application.command.paymentImport.payment.PaymentImportRequest;
import com.kynsoft.finamer.payment.application.query.paymentImport.payment.PaymentImportSearchErrorQuery;

public interface IPaymentImportService {

    void importPaymentFromFile(PaymentImportRequest request);
//    PaymentImportStatusResponse getPaymentImportStatus(PaymentImportStatusQuery query);

    PaginatedResponse getPaymentImportErrors(PaymentImportSearchErrorQuery query);
}
