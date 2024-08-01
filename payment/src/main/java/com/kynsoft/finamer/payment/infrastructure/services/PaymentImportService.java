package com.kynsoft.finamer.payment.infrastructure.services;

import com.kynsof.share.core.application.excel.ReaderConfiguration;
import com.kynsof.share.core.domain.request.PageableUtil;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsoft.finamer.payment.application.command.paymentImport.payment.PaymentImportRequest;
import com.kynsoft.finamer.payment.application.query.paymentImport.payment.PaymentImportSearchErrorQuery;
import com.kynsoft.finamer.payment.application.query.paymentImport.payment.PaymentImportStatusQuery;
import com.kynsoft.finamer.payment.application.query.paymentImport.payment.PaymentImportStatusResponse;
import com.kynsoft.finamer.payment.domain.dtoEnum.EImportPaymentType;
import com.kynsoft.finamer.payment.domain.services.IPaymentImportHelperService;
import com.kynsoft.finamer.payment.domain.services.IPaymentImportService;
import com.kynsoft.finamer.payment.infrastructure.excel.event.PaymentImportProcessEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

@Service
public class PaymentImportService implements IPaymentImportService {

    private final ApplicationEventPublisher paymentEventPublisher;
    private final IPaymentImportHelperService paymentImportHelperService;
    public PaymentImportService(ApplicationEventPublisher paymentEventPublisher,
                                IPaymentImportHelperService paymentImportHelperService
    ) {
        this.paymentEventPublisher = paymentEventPublisher;
        this.paymentImportHelperService = paymentImportHelperService;
    }

    @Override
    public void importPaymentFromFile(PaymentImportRequest request) {
        ReaderConfiguration readerConfiguration = new ReaderConfiguration();
        readerConfiguration.setIgnoreHeaders(true);
        InputStream inputStream = new ByteArrayInputStream(request.getFile());
        readerConfiguration.setInputStream(inputStream);
        readerConfiguration.setReadLastActiveSheet(true);
        paymentEventPublisher.publishEvent(new PaymentImportProcessEvent(request.getImportProcessId()));
        if (EImportPaymentType.EXPENSE.equals(request.getImportPaymentType())){
            paymentImportHelperService.readExcelForExpense(readerConfiguration,request);
        }else{
            paymentImportHelperService.readExcelForBank(readerConfiguration,request);
        }
        paymentImportHelperService.readPaymentCacheAndSave(request.getImportProcessId());
        paymentEventPublisher.publishEvent(new PaymentImportProcessEvent(request.getImportProcessId()));
        paymentImportHelperService.clearPaymentImportCache(request.getImportProcessId());
    }

    @Override
    public PaymentImportStatusResponse getPaymentImportStatus(PaymentImportStatusQuery query) {
        String status = paymentImportHelperService.getPaymentImportProcessStatus(query.getImportProcessId());
        return new PaymentImportStatusResponse(status);
    }

    @Override
    public PaginatedResponse getPaymentImportErrors(PaymentImportSearchErrorQuery query) {
        return paymentImportHelperService.getPaymentImportErrors(query.getSearchRequest().getQuery(),PageableUtil.createPageable(query.getSearchRequest()));
    }
}
