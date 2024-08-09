package com.kynsoft.finamer.payment.infrastructure.services;

import com.kynsof.share.core.application.excel.ReaderConfiguration;
import com.kynsof.share.core.domain.request.FilterCriteria;
import com.kynsof.share.core.domain.request.PageableUtil;
import com.kynsof.share.core.domain.request.SearchRequest;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsoft.finamer.payment.application.command.paymentImport.payment.PaymentImportRequest;
import com.kynsoft.finamer.payment.application.query.paymentImport.payment.PaymentImportSearchErrorQuery;
import com.kynsoft.finamer.payment.domain.dtoEnum.EImportPaymentType;
import com.kynsoft.finamer.payment.domain.services.AbstractPaymentImportHelperService;
import com.kynsoft.finamer.payment.domain.services.IPaymentImportService;
import com.kynsoft.finamer.payment.infrastructure.excel.event.process.PaymentImportProcessEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Objects;

@Service
public class PaymentImportService implements IPaymentImportService {

    private final ApplicationEventPublisher paymentEventPublisher;
    private final PaymentImportHelperProvider providerImportHelperService;

    public PaymentImportService(ApplicationEventPublisher paymentEventPublisher,
                                PaymentImportHelperProvider providerImportHelperService
    ) {
        this.paymentEventPublisher = paymentEventPublisher;
        this.providerImportHelperService = providerImportHelperService;
    }

    @Override
    public void importPaymentFromFile(PaymentImportRequest request) {
        ReaderConfiguration readerConfiguration = new ReaderConfiguration();
        readerConfiguration.setIgnoreHeaders(true);
        InputStream inputStream = new ByteArrayInputStream(request.getFile());
        readerConfiguration.setInputStream(inputStream);
        readerConfiguration.setReadLastActiveSheet(true);
        paymentEventPublisher.publishEvent(new PaymentImportProcessEvent(request.getImportProcessId()));
        AbstractPaymentImportHelperService paymentImportHelperService = providerImportHelperService.
                provideImportHelperService(request.getImportPaymentType());
        paymentImportHelperService.readExcel(readerConfiguration, request);
        paymentImportHelperService.readPaymentCacheAndSave(request);
        paymentEventPublisher.publishEvent(new PaymentImportProcessEvent(request.getImportProcessId()));
        paymentImportHelperService.clearPaymentImportCache(request.getImportProcessId());
    }


    @Override
    public PaginatedResponse getPaymentImportErrors(PaymentImportSearchErrorQuery query) {
        SearchRequest searchRequest = query.getSearchRequest();
        String importProcessId = searchRequest.getQuery();
        FilterCriteria filterCriteria = query.getSearchRequest().getFilter().get(0);
        Objects.requireNonNull(filterCriteria);
        Objects.requireNonNull(filterCriteria.getValue());
        EImportPaymentType eImportPaymentType =EImportPaymentType.valueOf((String) filterCriteria.getValue());
        AbstractPaymentImportHelperService paymentImportHelperService = providerImportHelperService.provideImportHelperService(eImportPaymentType);
        return paymentImportHelperService.getPaymentImportErrors(importProcessId, PageableUtil.createPageable(query.getSearchRequest()));
    }
}
