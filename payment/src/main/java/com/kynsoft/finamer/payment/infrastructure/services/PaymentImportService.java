package com.kynsoft.finamer.payment.infrastructure.services;

import com.kynsof.share.core.application.excel.ReaderConfiguration;
import com.kynsof.share.core.domain.exception.ExcelException;
import com.kynsof.share.core.domain.request.FilterCriteria;
import com.kynsof.share.core.domain.request.PageableUtil;
import com.kynsof.share.core.domain.request.SearchRequest;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsoft.finamer.payment.application.command.paymentImport.payment.PaymentImportRequest;
import com.kynsoft.finamer.payment.application.query.paymentImport.details.status.PaymentImportDetailStatusQuery;
import com.kynsoft.finamer.payment.application.query.paymentImport.details.status.PaymentImportDetailStatusResponse;
import com.kynsoft.finamer.payment.application.query.paymentImport.payment.error.PaymentImportSearchErrorQuery;
import com.kynsoft.finamer.payment.application.query.paymentImport.payment.status.PaymentImportStatusQuery;
import com.kynsoft.finamer.payment.application.query.paymentImport.payment.status.PaymentImportStatusResponse;
import com.kynsoft.finamer.payment.domain.dto.PaymentImportStatusDto;
import com.kynsoft.finamer.payment.domain.dtoEnum.EImportPaymentType;
import com.kynsoft.finamer.payment.domain.dtoEnum.EPaymentImportProcessStatus;
import com.kynsoft.finamer.payment.domain.excel.PaymentImportProcessStatusEntity;
import com.kynsoft.finamer.payment.domain.services.AbstractPaymentImportHelperService;
import com.kynsoft.finamer.payment.domain.services.IPaymentImportService;
import com.kynsoft.finamer.payment.infrastructure.excel.event.process.PaymentImportProcessEvent;
import com.kynsoft.finamer.payment.infrastructure.repository.redis.PaymentImportProcessStatusRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Objects;

@Service
public class PaymentImportService implements IPaymentImportService {

    private final ApplicationEventPublisher paymentEventPublisher;
    private final PaymentImportHelperProvider providerImportHelperService;

    private final PaymentImportProcessStatusRepository statusRepository;

    public PaymentImportService(ApplicationEventPublisher paymentEventPublisher,
                                PaymentImportHelperProvider providerImportHelperService,
                                PaymentImportProcessStatusRepository statusRepository
    ) {
        this.paymentEventPublisher = paymentEventPublisher;
        this.providerImportHelperService = providerImportHelperService;
        this.statusRepository = statusRepository;
    }

    @Async
    @Override
    public void importPaymentFromFile(PaymentImportRequest request) {
        AbstractPaymentImportHelperService paymentImportHelperService = providerImportHelperService.
                provideImportHelperService(request.getImportPaymentType());
        try {
            ReaderConfiguration readerConfiguration = new ReaderConfiguration();
            readerConfiguration.setIgnoreHeaders(true);
            InputStream inputStream = new ByteArrayInputStream(request.getFile());
            readerConfiguration.setInputStream(inputStream);
            readerConfiguration.setReadLastActiveSheet(true);
            readerConfiguration.setStrictHeaderOrder(true);
            paymentEventPublisher.publishEvent(new PaymentImportProcessEvent(this,
                   new PaymentImportStatusDto(EPaymentImportProcessStatus.RUNNING.name(),
                    request.getImportProcessId())));
            paymentImportHelperService.readExcel(readerConfiguration, request);
        }catch (ExcelException e){
            paymentEventPublisher.publishEvent(new PaymentImportProcessEvent(this,
                    new PaymentImportStatusDto(null,EPaymentImportProcessStatus.FINISHED.name(),
                            request.getImportProcessId(),true,e.getMessage())));
            throw new RuntimeException(e);
        }
            paymentImportHelperService.readPaymentCacheAndSave(request);
            paymentEventPublisher.publishEvent(new PaymentImportProcessEvent(this,
                    new PaymentImportStatusDto(EPaymentImportProcessStatus.FINISHED.name(), request.getImportProcessId())));
            paymentImportHelperService.clearPaymentImportCache(request.getImportProcessId());

    }

    @Override
    public PaymentImportStatusResponse getPaymentImportStatus(PaymentImportStatusQuery query) {
        PaymentImportStatusDto paymentImportStatusDto = statusRepository.findByImportProcessId(query.getImportProcessId())
                .map(PaymentImportProcessStatusEntity::toAggregate)
                .orElseThrow();
        if (paymentImportStatusDto.isHasError()){
            throw new ExcelException(paymentImportStatusDto.getExceptionMessage());
        }
        return new PaymentImportStatusResponse(paymentImportStatusDto.getStatus()) ;
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
