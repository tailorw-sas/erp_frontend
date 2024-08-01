package com.kynsoft.finamer.payment.infrastructure.services;

import com.kynsof.share.core.application.excel.ReaderConfiguration;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsoft.finamer.payment.application.command.paymentImport.detail.PaymentImportDetailRequest;
import com.kynsoft.finamer.payment.application.query.paymentImport.details.PaymentImportDetailSearchErrorQuery;
import com.kynsoft.finamer.payment.application.query.paymentImport.details.PaymentImportDetailStatusQuery;
import com.kynsoft.finamer.payment.application.query.paymentImport.details.PaymentImportDetailStatusResponse;
import com.kynsoft.finamer.payment.domain.services.IPaymentImportDetailHelperService;
import com.kynsoft.finamer.payment.domain.services.IPaymentImportDetailService;
import com.kynsoft.finamer.payment.infrastructure.excel.event.PaymentImportProcessEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

@Service
public class PaymentImportDetailServiceImpl implements IPaymentImportDetailService {
    private final ApplicationEventPublisher paymentEventPublisher;
    private final IPaymentImportDetailHelperService paymentImportDetailHelperService;

    public PaymentImportDetailServiceImpl(ApplicationEventPublisher paymentEventPublisher, IPaymentImportDetailHelperService paymentImportDetailHelperService) {
        this.paymentEventPublisher = paymentEventPublisher;
        this.paymentImportDetailHelperService = paymentImportDetailHelperService;
    }

    @Override
    public void importPaymentFromFile(PaymentImportDetailRequest request) {
        ReaderConfiguration readerConfiguration = new ReaderConfiguration();
        readerConfiguration.setIgnoreHeaders(true);
        InputStream inputStream = new ByteArrayInputStream(request.getFile());
        readerConfiguration.setInputStream(inputStream);
        readerConfiguration.setReadLastActiveSheet(true);
        paymentEventPublisher.publishEvent(new PaymentImportProcessEvent(request.getImportProcessId()));
        paymentImportDetailHelperService.readExcelForAntiIncome(readerConfiguration, request);
        paymentImportDetailHelperService.readPaymentCacheAndSave(request.getImportProcessId());
        paymentEventPublisher.publishEvent(new PaymentImportProcessEvent(request.getImportProcessId()));
        paymentImportDetailHelperService.clearPaymentImportCache(request.getImportProcessId());

    }

    @Override
    public PaymentImportDetailStatusResponse getPaymentImportStatus(PaymentImportDetailStatusQuery query) {
        return new PaymentImportDetailStatusResponse(paymentImportDetailHelperService.getPaymentImportProcessStatus(query.getImportProcessId()));
    }

    @Override
    public PaginatedResponse getPaymentImportErrors(PaymentImportDetailSearchErrorQuery query) {
        return paymentImportDetailHelperService.getPaymentImportErrors(query.getImportProcessId(),query.getPageable());
    }
}
