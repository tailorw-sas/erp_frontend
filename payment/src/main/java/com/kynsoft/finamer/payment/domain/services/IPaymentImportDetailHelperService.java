package com.kynsoft.finamer.payment.domain.services;

import com.kynsof.share.core.application.excel.ReaderConfiguration;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsoft.finamer.payment.application.command.paymentImport.detail.PaymentImportDetailRequest;
import com.kynsoft.finamer.payment.application.command.paymentImport.payment.PaymentImportRequest;
import com.kynsoft.finamer.payment.domain.excel.bean.Row;
import org.springframework.data.domain.Pageable;

public interface IPaymentImportDetailHelperService {


    public void readExcelForAntiIncome(ReaderConfiguration readerConfiguration, PaymentImportDetailRequest request);

    public void cachingPaymentImport(Row paymentRow);

    public void clearPaymentImportCache(String importProcessId);

    public void readPaymentCacheAndSave(String importProcessId);

    String getPaymentImportProcessStatus(String importProcessId);

    PaginatedResponse getPaymentImportErrors(String importProcessId, Pageable pageable);
}
