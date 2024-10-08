package com.kynsoft.finamer.invoicing.infrastructure.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.kynsof.share.core.application.excel.ExcelBean;
import com.kynsof.share.core.application.excel.ReaderConfiguration;
import com.kynsof.share.core.infrastructure.excel.ExcelBeanReader;
import com.kynsoft.finamer.invoicing.application.command.manageAttachment.create.CreateAttachmentCommand;
import com.kynsoft.finamer.invoicing.application.command.manageInvoice.reconcileAuto.InvoiceReconcileAutomaticRequest;
import com.kynsoft.finamer.invoicing.domain.dto.InvoiceReconcileAutomaticImportProcessDto;
import com.kynsoft.finamer.invoicing.domain.dto.InvoiceReconcileImportProcessStatusDto;
import com.kynsoft.finamer.invoicing.domain.dto.ManageAttachmentTypeDto;
import com.kynsoft.finamer.invoicing.domain.dtoEnum.EInvoiceReportType;
import com.kynsoft.finamer.invoicing.domain.dtoEnum.EProcessStatus;
import com.kynsoft.finamer.invoicing.domain.event.importStatus.CreateImportStatusEvent;
import com.kynsoft.finamer.invoicing.domain.excel.bean.BookingRow;
import com.kynsoft.finamer.invoicing.domain.excel.bean.reconcileAutomatic.InvoiceReconcileAutomaticRow;
import com.kynsoft.finamer.invoicing.domain.services.IInvoiceReconcileAutomaticService;
import com.kynsoft.finamer.invoicing.domain.services.IInvoiceReport;
import com.kynsoft.finamer.invoicing.domain.services.IManageAttachmentTypeService;
import com.kynsoft.finamer.invoicing.domain.services.IManageBookingService;
import com.kynsoft.finamer.invoicing.infrastructure.excel.validators.reconcileauto.ReconcileAutomaticValidatorFactory;
import com.kynsoft.finamer.invoicing.infrastructure.identity.ManageBooking;
import com.kynsoft.finamer.invoicing.infrastructure.identity.redis.reconcile.automatic.InvoiceReconcileAutomaticImportCacheEntity;
import com.kynsoft.finamer.invoicing.infrastructure.identity.redis.reconcile.automatic.InvoiceReconcileAutomaticImportErrorEntity;
import com.kynsoft.finamer.invoicing.infrastructure.repository.redis.reconcileAutomatic.reconcile.InvoiceReconcileAutomaticImportCacheRedisRepository;
import com.kynsoft.finamer.invoicing.infrastructure.repository.redis.reconcileAutomatic.reconcile.InvoiceReconcileAutomaticImportErrorRedisRepository;
import com.kynsoft.finamer.invoicing.infrastructure.services.report.content.InvoiceReconcileAutomaticProvider;
import com.kynsoft.finamer.invoicing.infrastructure.services.report.factory.InvoiceReportProviderFactory;
import com.kynsoft.finamer.invoicing.infrastructure.utils.InvoiceUploadAttachmentUtil;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Optional;

@Service
public class InvoiceReconcileAutomaticServiceImpl implements IInvoiceReconcileAutomaticService {

    private final ApplicationEventPublisher applicationEventPublisher;
    private final InvoiceReconcileAutomaticImportCacheRedisRepository cacheRedisRepository;
    private final InvoiceReconcileAutomaticImportErrorRedisRepository errorRedisRepository;
    private final IManageBookingService bookingService;
    private final ReconcileAutomaticValidatorFactory reconcileAutomaticValidatorFactory;
    private final InvoiceReportProviderFactory invoiceReportProviderFactory;
    private final InvoiceUploadAttachmentUtil invoiceUploadAttachmentUtil;
    private final IManageAttachmentTypeService typeService;

    public InvoiceReconcileAutomaticServiceImpl(ApplicationEventPublisher applicationEventPublisher,
                                                InvoiceReconcileAutomaticImportCacheRedisRepository cacheRedisRepository,
                                                InvoiceReconcileAutomaticImportErrorRedisRepository errorRedisRepository,
                                                IManageBookingService bookingService, ReconcileAutomaticValidatorFactory reconcileAutomaticValidatorFactory,
                                                InvoiceReportProviderFactory invoiceReportProviderFactory, InvoiceUploadAttachmentUtil invoiceUploadAttachmentUtil, IManageAttachmentTypeService typeService,
                                                ) {
        this.applicationEventPublisher = applicationEventPublisher;
        this.cacheRedisRepository = cacheRedisRepository;
        this.errorRedisRepository = errorRedisRepository;
        this.bookingService = bookingService;
        this.reconcileAutomaticValidatorFactory = reconcileAutomaticValidatorFactory;

        this.invoiceReportProviderFactory = invoiceReportProviderFactory;
        this.invoiceUploadAttachmentUtil = invoiceUploadAttachmentUtil;
        this.typeService = typeService;
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
        reconcileAutomaticValidatorFactory.createValidators("");
        for (InvoiceReconcileAutomaticRow invoiceReconcileAutomaticRow : excelBean) {
            invoiceReconcileAutomaticRow.setImportProcessId(request.getImportProcessId());
            if (reconcileAutomaticValidatorFactory.validate(invoiceReconcileAutomaticRow)) {
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

        }
        return 0;
    }

    private List<InvoiceReconcileAutomaticImportCacheEntity> validateImportation(InvoiceReconcileAutomaticRequest request) {
        Pageable pageable = PageRequest.of(0, 500, Sort.by(Sort.Direction.ASC, "id"));
        Page<InvoiceReconcileAutomaticImportCacheEntity> cachePage;
        do {
            cachePage = cacheRedisRepository.findAllByImportProcessId(request.getImportProcessId(), pageable);


            pageable = pageable.next();
            //Validar la importacion y en caso de errores crear los errores en bd
            //devolver los valores en buen estado
        }  while (cachePage.hasNext()) ;

    }


    private Optional<byte[]> createInvoiceReconcileAutomaticSupportAttachmentContent(String invoiceId) {
       IInvoiceReport service= invoiceReportProviderFactory.getInvoiceReportService(EInvoiceReportType.RECONCILE_AUTO);
      return service.generateReport(invoiceId);
    }

    private void createAttachmentForInvoice(String invoiceId,String employeeId,byte[] fileContent){
        try {
            Optional<ManageAttachmentTypeDto> attachmentTypeDto = typeService.findDefault();
            if (attachmentTypeDto.isPresent()) {
                LinkedHashMap<String, String> response = invoiceUploadAttachmentUtil.uploadAttachmentContent("Reconcile automatic", fileContent);
                CreateAttachmentCommand createAttachmentCommand = new CreateAttachmentCommand("Reconcile Automatic Support", response.get("url"),
                        "Importer by reconcile automatic",attachmentTypeDto.get(),invoiceId, employeeId,)
            }
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
    private void createImportProcessStatusEvent(InvoiceReconcileAutomaticImportProcessDto dto) {
        CreateImportStatusEvent createImportStatusEvent = new CreateImportStatusEvent(this, dto);
        applicationEventPublisher.publishEvent(createImportStatusEvent);
    }
}
