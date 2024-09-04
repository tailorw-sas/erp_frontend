package com.kynsoft.finamer.payment.infrastructure.services;

import com.kynsof.share.core.application.excel.ReaderConfiguration;
import com.kynsof.share.core.domain.exception.ExcelException;
import com.kynsof.share.core.domain.request.FilterCriteria;
import com.kynsof.share.core.domain.request.PageableUtil;
import com.kynsof.share.core.domain.request.SearchRequest;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsoft.finamer.payment.application.command.paymentImport.detail.PaymentImportDetailRequest;
import com.kynsoft.finamer.payment.application.query.paymentImport.details.error.PaymentImportDetailSearchErrorQuery;
import com.kynsoft.finamer.payment.application.query.paymentImport.details.status.PaymentImportDetailStatusQuery;
import com.kynsoft.finamer.payment.application.query.paymentImport.details.status.PaymentImportDetailStatusResponse;
import com.kynsoft.finamer.payment.domain.dto.PaymentImportStatusDto;
import com.kynsoft.finamer.payment.domain.dtoEnum.EImportPaymentType;
import com.kynsoft.finamer.payment.domain.dtoEnum.EPaymentImportProcessStatus;
import com.kynsoft.finamer.payment.domain.excel.PaymentImportProcessStatusEntity;
import com.kynsoft.finamer.payment.domain.services.AbstractPaymentImportHelperService;
import com.kynsoft.finamer.payment.domain.services.IPaymentImportDetailService;
import com.kynsoft.finamer.payment.infrastructure.excel.event.createAttachment.CreateAttachmentEvent;
import com.kynsoft.finamer.payment.infrastructure.excel.event.process.PaymentImportProcessEvent;
import com.kynsoft.finamer.payment.infrastructure.repository.redis.PaymentImportProcessStatusRepository;
import com.kynsoft.finamer.payment.infrastructure.services.helpers.PaymentImportAntiIncomeHelperServiceImpl;
import org.slf4j.ILoggerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Objects;
import java.util.UUID;

@Service
public class PaymentImportDetailServiceImpl implements IPaymentImportDetailService {

    private final Logger log = LoggerFactory.getLogger(PaymentImportDetailServiceImpl.class);
    private final ApplicationEventPublisher paymentEventPublisher;
    private final PaymentImportHelperProvider paymentImportHelperProvider;

    private final PaymentImportProcessStatusRepository statusRepository;

    public PaymentImportDetailServiceImpl(ApplicationEventPublisher paymentEventPublisher,
                                          PaymentImportHelperProvider paymentImportHelperProvider1,
                                          PaymentImportProcessStatusRepository statusRepository) {
        this.paymentEventPublisher = paymentEventPublisher;

        this.paymentImportHelperProvider = paymentImportHelperProvider1;
        this.statusRepository = statusRepository;
    }

    @Async
    @Override
    public void importPaymentFromFile(PaymentImportDetailRequest request) {
        AbstractPaymentImportHelperService paymentImportHelperService =
                paymentImportHelperProvider.provideImportHelperService(request.getImportPaymentType());
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
        } catch (ExcelException e) {
            paymentEventPublisher.publishEvent(new PaymentImportProcessEvent(this,
                    new PaymentImportStatusDto(null, EPaymentImportProcessStatus.FINISHED.name(),
                            request.getImportProcessId(), true, e.getMessage())));
            paymentImportHelperService.clearPaymentImportCache(request.getImportProcessId());
            log.error(e.getMessage());
            return;
        }
        try {
            if (EImportPaymentType.ANTI.equals(request.getImportPaymentType())){
                ((PaymentImportAntiIncomeHelperServiceImpl) paymentImportHelperService).createAttachment(request);
            }
            paymentImportHelperService.readPaymentCacheAndSave(request);
        }catch (Exception e){
            paymentImportHelperService.clearPaymentImportCache(request.getImportProcessId());
            paymentEventPublisher.publishEvent(new PaymentImportProcessEvent(this,
                    new PaymentImportStatusDto(null, EPaymentImportProcessStatus.FINISHED.name(),
                            request.getImportProcessId(), true, "Excel couldn't be imported")));
            log.error(e.getMessage());
            return;
        }
        paymentImportHelperService.clearPaymentImportCache(request.getImportProcessId());
        paymentEventPublisher.publishEvent(new PaymentImportProcessEvent(this,
                new PaymentImportStatusDto(EPaymentImportProcessStatus.FINISHED.name(), request.getImportProcessId())));

    }

    @Override
    public PaymentImportDetailStatusResponse getPaymentImportStatus(PaymentImportDetailStatusQuery query) {
        PaymentImportStatusDto paymentImportStatusDto = statusRepository.findByImportProcessId(query.getImportProcessId())
                .map(PaymentImportProcessStatusEntity::toAggregate)
                .orElse(new PaymentImportStatusDto());
        if (paymentImportStatusDto.isHasError()) {
            throw new ExcelException(paymentImportStatusDto.getExceptionMessage());
        }
        return new PaymentImportDetailStatusResponse(paymentImportStatusDto.getStatus());
    }


    @Override
    public PaginatedResponse getPaymentImportErrors(PaymentImportDetailSearchErrorQuery query) {
        SearchRequest searchRequest = query.getSearchRequest();
        String importProcessId = searchRequest.getQuery();
        FilterCriteria filterCriteria = query.getSearchRequest().getFilter().get(0);
        Objects.requireNonNull(filterCriteria);
        Objects.requireNonNull(filterCriteria.getValue());
        EImportPaymentType eImportPaymentType = EImportPaymentType.valueOf((String) filterCriteria.getValue());
        AbstractPaymentImportHelperService paymentImportHelperService = paymentImportHelperProvider.provideImportHelperService(eImportPaymentType);
        return paymentImportHelperService.getPaymentImportErrors(importProcessId, PageableUtil.createPageable(query.getSearchRequest()));
    }
}
