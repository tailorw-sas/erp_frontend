package com.kynsoft.finamer.payment.infrastructure.services;

import com.kynsof.share.core.application.excel.ReaderConfiguration;
import com.kynsof.share.core.domain.request.FilterCriteria;
import com.kynsof.share.core.domain.request.PageableUtil;
import com.kynsof.share.core.domain.request.SearchRequest;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsoft.finamer.payment.application.command.paymentImport.detail.PaymentImportDetailRequest;
import com.kynsoft.finamer.payment.application.query.paymentImport.details.PaymentImportDetailSearchErrorQuery;
import com.kynsoft.finamer.payment.domain.dtoEnum.EImportPaymentType;
import com.kynsoft.finamer.payment.domain.services.AbstractPaymentImportHelperService;
import com.kynsoft.finamer.payment.domain.services.IPaymentImportDetailService;
import com.kynsoft.finamer.payment.infrastructure.excel.event.process.PaymentImportProcessEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
public class PaymentImportDetailServiceImpl implements IPaymentImportDetailService {
    private final ApplicationEventPublisher paymentEventPublisher;
    private final PaymentImportHelperProvider paymentImportHelperProvider;

    public PaymentImportDetailServiceImpl(ApplicationEventPublisher paymentEventPublisher,
                                          PaymentImportHelperProvider paymentImportHelperProvider1) {
        this.paymentEventPublisher = paymentEventPublisher;

        this.paymentImportHelperProvider = paymentImportHelperProvider1;
    }

    @Override
    public List<UUID> importPaymentFromFile(PaymentImportDetailRequest request) {
        ReaderConfiguration readerConfiguration = new ReaderConfiguration();
        readerConfiguration.setIgnoreHeaders(true);
        InputStream inputStream = new ByteArrayInputStream(request.getFile());
        readerConfiguration.setInputStream(inputStream);
        readerConfiguration.setReadLastActiveSheet(true);
        paymentEventPublisher.publishEvent(new PaymentImportProcessEvent(request.getImportProcessId()));
        AbstractPaymentImportHelperService paymentImportHelperService = paymentImportHelperProvider.provideImportHelperService(request.getImportPaymentType());
        List<UUID> paymentsId;
        paymentImportHelperService.readExcel(readerConfiguration, request);
        paymentsId = paymentImportHelperService.readPaymentCacheAndSave(request);
        paymentEventPublisher.publishEvent(new PaymentImportProcessEvent(request.getImportProcessId()));
        paymentImportHelperService.clearPaymentImportCache(request.getImportProcessId());
        return paymentsId;

    }


    @Override
    public PaginatedResponse getPaymentImportErrors(PaymentImportDetailSearchErrorQuery query) {
        SearchRequest searchRequest = query.getSearchRequest();
        String importProcessId = searchRequest.getQuery();
        FilterCriteria filterCriteria = query.getSearchRequest().getFilter().get(0);
        Objects.requireNonNull(filterCriteria);
        Objects.requireNonNull(filterCriteria.getValue());
        EImportPaymentType eImportPaymentType =EImportPaymentType.valueOf((String) filterCriteria.getValue());
        AbstractPaymentImportHelperService paymentImportHelperService = paymentImportHelperProvider.provideImportHelperService(eImportPaymentType);
        return paymentImportHelperService.getPaymentImportErrors(importProcessId, PageableUtil.createPageable(query.getSearchRequest()));
    }
}
