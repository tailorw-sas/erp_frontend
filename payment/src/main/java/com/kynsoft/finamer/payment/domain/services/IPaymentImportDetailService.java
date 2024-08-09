package com.kynsoft.finamer.payment.domain.services;

import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsoft.finamer.payment.application.command.paymentImport.detail.PaymentImportDetailRequest;
import com.kynsoft.finamer.payment.application.query.paymentImport.details.PaymentImportDetailSearchErrorQuery;

import java.util.List;
import java.util.UUID;

public interface IPaymentImportDetailService {
    List<UUID> importPaymentFromFile(PaymentImportDetailRequest request);
//    PaymentImportDetailStatusResponse getPaymentImportStatus(PaymentImportDetailStatusQuery query);

    PaginatedResponse getPaymentImportErrors(PaymentImportDetailSearchErrorQuery query);
}
