package com.kynsoft.finamer.payment.infrastructure.services;

import com.kynsof.share.core.application.excel.ReaderConfiguration;
import com.kynsof.share.core.domain.exception.ExcelException;
import com.kynsof.share.core.domain.request.PageableUtil;
import com.kynsof.share.core.domain.request.SearchRequest;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsoft.finamer.payment.application.command.paymentImport.payment.PaymentImportRequest;
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
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

@Service
public class PaymentExpenseToBookingImportServiceImpl implements IPaymentImportService {

    private final ApplicationEventPublisher paymentEventPublisher;
    private final PaymentImportHelperProvider providerImportHelperService;

    private final PaymentImportProcessStatusRepository statusRepository;
    private AbstractPaymentImportHelperService paymentImportHelperService;

    public PaymentExpenseToBookingImportServiceImpl(ApplicationEventPublisher paymentEventPublisher,
                                                    PaymentImportHelperProvider providerImportHelperService,
                                                    PaymentImportProcessStatusRepository statusRepository) {
        this.paymentEventPublisher = paymentEventPublisher;
        this.providerImportHelperService = providerImportHelperService;
        this.statusRepository = statusRepository;
    }

    @Override
    public void importPaymentFromFile(PaymentImportRequest request) {
        paymentImportHelperService = providerImportHelperService.
                provideImportHelperService(EImportPaymentType.EXPENSE_TO_BOOKING);
        try {
            ReaderConfiguration readerConfiguration = new ReaderConfiguration();
            readerConfiguration.setIgnoreHeaders(true);
            InputStream inputStream = new ByteArrayInputStream(request.getFile());
            readerConfiguration.setInputStream(inputStream);
            readerConfiguration.setReadLastActiveSheet(true);
            //readerConfiguration.setStrictHeaderOrder(true);
            paymentEventPublisher.publishEvent(new PaymentImportProcessEvent(this,
                    new PaymentImportStatusDto(EPaymentImportProcessStatus.RUNNING.name(),
                            request.getImportProcessId())));
            paymentImportHelperService.readExcel(readerConfiguration, request);
            paymentImportHelperService.readPaymentCacheAndSave(request);
            paymentEventPublisher.publishEvent(new PaymentImportProcessEvent(this,
                    new PaymentImportStatusDto(EPaymentImportProcessStatus.FINISHED.name(), request.getImportProcessId())));
            paymentImportHelperService.clearPaymentImportCache(request.getImportProcessId());
        } catch (ExcelException e) {
            paymentEventPublisher.publishEvent(new PaymentImportProcessEvent(this,
                    new PaymentImportStatusDto(null, EPaymentImportProcessStatus.FINISHED.name(),
                            request.getImportProcessId(), true, e.getMessage())));
            throw new RuntimeException(e);
        } catch (Exception e) {
            paymentEventPublisher.publishEvent(new PaymentImportProcessEvent(this,
                    new PaymentImportStatusDto(null, EPaymentImportProcessStatus.FINISHED.name(),
                            request.getImportProcessId(), true, "The import cannot be performed.")));
            throw new RuntimeException(e);
        }
    }

    @Override
    public PaymentImportStatusResponse getPaymentImportStatus(PaymentImportStatusQuery query) {
        PaymentImportStatusDto paymentImportStatusDto = statusRepository.findByImportProcessId(query.getImportProcessId())
                .map(PaymentImportProcessStatusEntity::toAggregate)
                .orElseThrow();
        if (paymentImportStatusDto.isHasError()) {
            throw new ExcelException(paymentImportStatusDto.getExceptionMessage());
        }
        return new PaymentImportStatusResponse(paymentImportStatusDto.getStatus(), paymentImportHelperService.getTotalProcessRow());
    }

    @Override
    public PaginatedResponse getPaymentImportErrors(PaymentImportSearchErrorQuery query) {
        SearchRequest searchRequest = query.getSearchRequest();
        String importProcessId = searchRequest.getQuery();
        AbstractPaymentImportHelperService paymentImportHelperService = providerImportHelperService.provideImportHelperService(EImportPaymentType.EXPENSE_TO_BOOKING);
        return paymentImportHelperService.getPaymentImportErrors(importProcessId, PageableUtil.createPageable(query.getSearchRequest()));
    }
}
