package com.kynsoft.finamer.invoicing.infrastructure.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.kynsof.share.core.application.excel.ExcelBean;
import com.kynsof.share.core.application.excel.ReaderConfiguration;
import com.kynsof.share.core.domain.exception.ExcelException;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsof.share.core.infrastructure.bus.IMediator;
import com.kynsof.share.core.infrastructure.excel.ExcelBeanReader;
import com.kynsof.share.utils.ServiceLocator;
import com.kynsoft.finamer.invoicing.application.command.manageAttachment.create.CreateAttachmentCommand;
import com.kynsoft.finamer.invoicing.application.command.manageInvoice.reconcileAuto.InvoiceReconcileAutomaticRequest;
import com.kynsoft.finamer.invoicing.application.query.invoiceReconcile.processstatus.automatic.InvoiceReconcileAutomaticImportProcessStatusRequest;
import com.kynsoft.finamer.invoicing.application.query.invoiceReconcile.processstatus.automatic.InvoiceReconcileAutomaticImportProcessStatusResponse;
import com.kynsoft.finamer.invoicing.application.query.invoiceReconcile.reconcileError.automatic.InvoiceReconcileAutomaticImportErrorRequest;
import com.kynsoft.finamer.invoicing.application.query.manageBooking.importbooking.ImportBookingProcessStatusResponse;
import com.kynsoft.finamer.invoicing.domain.dto.InvoiceReconcileAutomaticImportProcessDto;
import com.kynsoft.finamer.invoicing.domain.dto.ManageAttachmentTypeDto;
import com.kynsoft.finamer.invoicing.domain.dto.ManageBookingDto;
import com.kynsoft.finamer.invoicing.domain.dtoEnum.EInvoiceReportType;
import com.kynsoft.finamer.invoicing.domain.dtoEnum.EProcessStatus;
import com.kynsoft.finamer.invoicing.domain.event.importStatus.CreateImportReconcileAutomaticStatusEvent;
import com.kynsoft.finamer.invoicing.domain.excel.bean.reconcileAutomatic.InvoiceReconcileAutomaticRow;
import com.kynsoft.finamer.invoicing.domain.services.IInvoiceReconcileAutomaticService;
import com.kynsoft.finamer.invoicing.domain.services.IInvoiceReport;
import com.kynsoft.finamer.invoicing.domain.services.IManageAttachmentTypeService;
import com.kynsoft.finamer.invoicing.domain.services.IManageBookingService;
import com.kynsoft.finamer.invoicing.infrastructure.excel.validators.reconcileauto.ReconcileAutomaticValidatorFactory;
import com.kynsoft.finamer.invoicing.infrastructure.identity.redis.reconcile.automatic.InvoiceReconcileAutomaticImportCacheEntity;
import com.kynsoft.finamer.invoicing.infrastructure.identity.redis.reconcile.automatic.InvoiceReconcileAutomaticImportErrorEntity;
import com.kynsoft.finamer.invoicing.infrastructure.identity.redis.reconcile.automatic.InvoiceReconcileAutomaticImportProcessStatusRedisEntity;
import com.kynsoft.finamer.invoicing.infrastructure.repository.redis.reconcileAutomatic.reconcile.InvoiceReconcileAutomaticImportCacheRedisRepository;
import com.kynsoft.finamer.invoicing.infrastructure.repository.redis.reconcileAutomatic.reconcile.InvoiceReconcileAutomaticImportErrorRedisRepository;
import com.kynsoft.finamer.invoicing.infrastructure.repository.redis.reconcileAutomatic.reconcile.InvoiceReconcileAutomaticImportProcessStatusRedisRepository;
import com.kynsoft.finamer.invoicing.infrastructure.services.report.factory.InvoiceReportProviderFactory;
import com.kynsoft.finamer.invoicing.infrastructure.utils.InvoiceUploadAttachmentUtil;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.LinkedHashMap;
import java.util.Optional;
import java.util.UUID;

@Service
public class InvoiceReconcileAutomaticServiceImpl implements IInvoiceReconcileAutomaticService {

    private final ApplicationEventPublisher applicationEventPublisher;
    private final InvoiceReconcileAutomaticImportCacheRedisRepository cacheRedisRepository;
    private final ReconcileAutomaticValidatorFactory reconcileAutomaticValidatorFactory;
    private final InvoiceReportProviderFactory invoiceReportProviderFactory;
    private final InvoiceUploadAttachmentUtil invoiceUploadAttachmentUtil;
    private final IManageAttachmentTypeService typeService;
    private final IManageBookingService manageBookingService;
    private final InvoiceReconcileAutomaticImportErrorRedisRepository errorRedisRepository;
    private final InvoiceReconcileAutomaticImportProcessStatusRedisRepository statusRedisRepository;
    private final ServiceLocator<IMediator> serviceLocator;

    public InvoiceReconcileAutomaticServiceImpl(ApplicationEventPublisher applicationEventPublisher,
                                                InvoiceReconcileAutomaticImportCacheRedisRepository cacheRedisRepository,
                                                ReconcileAutomaticValidatorFactory reconcileAutomaticValidatorFactory,
                                                InvoiceReportProviderFactory invoiceReportProviderFactory,
                                                InvoiceUploadAttachmentUtil invoiceUploadAttachmentUtil,
                                                IManageAttachmentTypeService typeService, IManageBookingService manageBookingService,
                                                InvoiceReconcileAutomaticImportErrorRedisRepository errorRedisRepository,
                                                InvoiceReconcileAutomaticImportProcessStatusRedisRepository statusRedisRepository,
                                                ServiceLocator<IMediator> serviceLocator
    ) {
        this.applicationEventPublisher = applicationEventPublisher;
        this.cacheRedisRepository = cacheRedisRepository;
        this.reconcileAutomaticValidatorFactory = reconcileAutomaticValidatorFactory;

        this.invoiceReportProviderFactory = invoiceReportProviderFactory;
        this.invoiceUploadAttachmentUtil = invoiceUploadAttachmentUtil;
        this.typeService = typeService;
        this.manageBookingService = manageBookingService;
        this.errorRedisRepository = errorRedisRepository;
        this.statusRedisRepository = statusRedisRepository;
        this.serviceLocator = serviceLocator;
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
        this.createReconcileAutomaticSupport(request);
        this.removeImportedData(request.getImportProcessId());
        this.createImportProcessStatusEvent(
                InvoiceReconcileAutomaticImportProcessDto.builder()
                        .status(EProcessStatus.FINISHED)
                        .importProcessId(request.getImportProcessId()).build()
        );
    }

    private void readExcel(InvoiceReconcileAutomaticRequest request) {
        ReaderConfiguration readerConfiguration = new ReaderConfiguration();
        readerConfiguration.setIgnoreHeaders(true);
        InputStream inputStream = new ByteArrayInputStream(request.getFileContent());
        readerConfiguration.setInputStream(inputStream);
        readerConfiguration.setReadLastActiveSheet(true);
        ExcelBeanReader<InvoiceReconcileAutomaticRow> reader = new ExcelBeanReader<>(readerConfiguration, InvoiceReconcileAutomaticRow.class);
        ExcelBean<InvoiceReconcileAutomaticRow> excelBean = new ExcelBean<>(reader);
        reconcileAutomaticValidatorFactory.createValidators(request.getInvoiceIds());
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
    }

    private void createReconcileAutomaticSupport(InvoiceReconcileAutomaticRequest request) {
        Pageable pageable = PageRequest.of(0, 500, Sort.by(Sort.Direction.ASC, "id"));
        Page<InvoiceReconcileAutomaticImportCacheEntity> cachePage;
        do {
            cachePage = cacheRedisRepository.findAllByImportProcessId(request.getImportProcessId(), pageable);
            cachePage.stream().forEach(cacheEntity -> {
                Optional<ManageBookingDto> booking = manageBookingService.findManageBookingByReservationNumber(cacheEntity.getReservationNumber());
                Optional<byte[]> fileContent = createInvoiceReconcileAutomaticSupportAttachmentContent(booking.get().getInvoice().getId().toString());
                fileContent.ifPresent(content -> createAttachmentForInvoice(booking.get().getInvoice().getId().toString(),
                        request.getEmployeeId(),request.getEmployeeName(), content));
            });
            pageable = pageable.next();
        } while (cachePage.hasNext());

    }

    private void removeImportedData(String importProcessId){
        Pageable pageable = PageRequest.of(0, 500, Sort.by(Sort.Direction.ASC, "id"));
        Page<InvoiceReconcileAutomaticImportCacheEntity> cachePage;
        do {
            cachePage = cacheRedisRepository.findAllByImportProcessId(importProcessId, pageable);
            cacheRedisRepository.deleteAll(cachePage.getContent());
            pageable = pageable.next();
        } while (cachePage.hasNext());
    }
    private Optional<byte[]> createInvoiceReconcileAutomaticSupportAttachmentContent(String invoiceId) {
        IInvoiceReport service = invoiceReportProviderFactory.getInvoiceReportService(EInvoiceReportType.RECONCILE_AUTO);
        return service.generateReport(invoiceId);
    }

    private void createAttachmentForInvoice(String invoiceId, String employeeId, String employeeName, byte[] fileContent) {
        try {
            Optional<ManageAttachmentTypeDto> attachmentTypeDto = typeService.findDefault();
            if (attachmentTypeDto.isPresent()) {
                LinkedHashMap<String, String> response = invoiceUploadAttachmentUtil.uploadAttachmentContent("Reconcile automatic", fileContent);
                CreateAttachmentCommand createAttachmentCommand = new CreateAttachmentCommand("Reconcile Automatic Support", response.get("url"),
                        "Importer by reconcile automatic", attachmentTypeDto.get().getId(),
                        UUID.fromString(invoiceId), employeeName, UUID.fromString(employeeId), null);
                IMediator mediator = serviceLocator.getBean(IMediator.class);
                mediator.send(createAttachmentCommand);
            }
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    private void createImportProcessStatusEvent(InvoiceReconcileAutomaticImportProcessDto dto) {
        CreateImportReconcileAutomaticStatusEvent createImportStatusEvent = new CreateImportReconcileAutomaticStatusEvent(this, dto);
        applicationEventPublisher.publishEvent(createImportStatusEvent);
    }

    @Override
    public PaginatedResponse getImportErrors(InvoiceReconcileAutomaticImportErrorRequest invoiceReconcileAutomaticErrorRequest) {
        Page<InvoiceReconcileAutomaticImportErrorEntity> importErrorPages = errorRedisRepository.
                findAllByImportProcessId(invoiceReconcileAutomaticErrorRequest.getQuery(),
                        invoiceReconcileAutomaticErrorRequest.getPageable());
        return new PaginatedResponse(importErrorPages.getContent(),
                importErrorPages.getTotalPages(),
                importErrorPages.getNumberOfElements(),
                importErrorPages.getTotalElements(),
                importErrorPages.getSize(),
                importErrorPages.getNumber());
    }

    @Override
    public InvoiceReconcileAutomaticImportProcessStatusResponse getImportProcessStatus(InvoiceReconcileAutomaticImportProcessStatusRequest importProcessStatusRequest) {
        Optional<InvoiceReconcileAutomaticImportProcessStatusRedisEntity> statusDtp =
                statusRedisRepository.findByImportProcessId(importProcessStatusRequest.getImportProcessId());
        if (statusDtp.isPresent()) {
            if (statusDtp.get().isHasError()) {
                throw new ExcelException(statusDtp.get().getExceptionMessage());
            }
            return new InvoiceReconcileAutomaticImportProcessStatusResponse(statusDtp.get().getStatus().name());
        }
        return null;
    }
}
