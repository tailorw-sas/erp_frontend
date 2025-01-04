package com.kynsoft.finamer.payment.domain.services;

import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsoft.finamer.payment.application.command.paymentImport.payment.PaymentImportRequest;
import com.kynsoft.finamer.payment.application.query.paymentImport.payment.error.PaymentImportSearchErrorQuery;
import com.kynsoft.finamer.payment.application.query.paymentImport.payment.status.PaymentImportStatusQuery;
import com.kynsoft.finamer.payment.application.query.paymentImport.payment.status.PaymentImportStatusResponse;

public interface IPaymentImportService {

    void importPaymentFromFile(PaymentImportRequest request);
    PaymentImportStatusResponse getPaymentImportStatus(PaymentImportStatusQuery query);

    PaginatedResponse getPaymentImportErrors(PaymentImportSearchErrorQuery query);
}
