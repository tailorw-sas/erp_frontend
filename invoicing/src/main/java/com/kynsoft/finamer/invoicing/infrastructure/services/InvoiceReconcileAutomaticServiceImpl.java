package com.kynsoft.finamer.invoicing.infrastructure.services;

import com.kynsof.share.core.application.excel.ExcelBean;
import com.kynsof.share.core.application.excel.ReaderConfiguration;
import com.kynsof.share.core.infrastructure.excel.ExcelBeanReader;
import com.kynsoft.finamer.invoicing.application.command.manageInvoice.reconcileAuto.InvoiceReconcileAutomaticRequest;
import com.kynsoft.finamer.invoicing.domain.dto.InvoiceReconcileAutomaticImportProcessDto;
import com.kynsoft.finamer.invoicing.domain.dto.InvoiceReconcileImportProcessStatusDto;
import com.kynsoft.finamer.invoicing.domain.dtoEnum.EProcessStatus;
import com.kynsoft.finamer.invoicing.domain.event.importStatus.CreateImportStatusEvent;
import com.kynsoft.finamer.invoicing.domain.excel.bean.BookingRow;
import com.kynsoft.finamer.invoicing.domain.excel.bean.reconcileAutomatic.InvoiceReconcileAutomaticRow;
import com.kynsoft.finamer.invoicing.domain.services.IInvoiceReconcileAutomaticService;
import com.kynsoft.finamer.invoicing.infrastructure.identity.redis.reconcile.automatic.InvoiceReconcileAutomaticImportCacheEntity;
import com.kynsoft.finamer.invoicing.infrastructure.repository.redis.reconcileAutomatic.reconcile.InvoiceReconcileAutomaticImportCacheRedisRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.util.List;

@Service
public class InvoiceReconcileAutomaticServiceImpl implements IInvoiceReconcileAutomaticService {

    private final ApplicationEventPublisher applicationEventPublisher;
    private final InvoiceReconcileAutomaticImportCacheRedisRepository cacheRedisRepository;

    public InvoiceReconcileAutomaticServiceImpl(ApplicationEventPublisher applicationEventPublisher,
                                                InvoiceReconcileAutomaticImportCacheRedisRepository cacheRedisRepository) {
        this.applicationEventPublisher = applicationEventPublisher;
        this.cacheRedisRepository = cacheRedisRepository;
    }

    @Override
    public void reconcileAutomatic(InvoiceReconcileAutomaticRequest request) {
        this.createImportProcessStatusEvent(
                InvoiceReconcileAutomaticImportProcessDto.builder()
                        .status(EProcessStatus.RUNNING)
                        .total(0)
                        .importProcessId(request.getImportProcessId()).build()
        );
        this.readExcel(request);

        this.cleanImportationResource(request.getImportProcessId());
        this.createImportProcessStatusEvent(
                InvoiceReconcileAutomaticImportProcessDto.builder()
                        .status(EProcessStatus.FINISHED)
                        .importProcessId(request.getImportProcessId()).build()
        );
    }

    private int readExcel(InvoiceReconcileAutomaticRequest request) {
        ReaderConfiguration readerConfiguration = new ReaderConfiguration();
        readerConfiguration.setIgnoreHeaders(true);
        InputStream inputStream = new ByteArrayInputStream(request.getFileContent());
        readerConfiguration.setInputStream(inputStream);
        readerConfiguration.setReadLastActiveSheet(true);
        ExcelBeanReader<InvoiceReconcileAutomaticRow> reader = new ExcelBeanReader<>(readerConfiguration, InvoiceReconcileAutomaticRow.class);
        ExcelBean<InvoiceReconcileAutomaticRow> excelBean = new ExcelBean<>(reader);
        for (InvoiceReconcileAutomaticRow invoiceReconcileAutomaticRow : excelBean) {
            invoiceReconcileAutomaticRow.setImportProcessId(request.getImportProcessId());
            InvoiceReconcileAutomaticImportCacheEntity cache = InvoiceReconcileAutomaticImportCacheEntity.builder()
                    .nightType(invoiceReconcileAutomaticRow.getColumnN())
                    .reservationNumber(invoiceReconcileAutomaticRow.getColumnW())
                    .couponNumber(invoiceReconcileAutomaticRow.getColumnE()
                            .concat(invoiceReconcileAutomaticRow.getColumnL()))
                    .price(invoiceReconcileAutomaticRow.getColumnAN())
                    .importProcessId(request.getImportProcessId())
                    .build();
            cacheRedisRepository.save(cache);

        }
        return 0;
    }

    private void createImportProcessStatusEvent(InvoiceReconcileAutomaticImportProcessDto dto) {
        CreateImportStatusEvent createImportStatusEvent = new CreateImportStatusEvent(this, dto);
        applicationEventPublisher.publishEvent(createImportStatusEvent);
    }
}
