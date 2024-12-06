package com.kynsoft.finamer.invoicing.infrastructure.services;

import com.kynsof.share.core.domain.exception.ExcelException;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsof.share.core.infrastructure.bus.IMediator;
import com.kynsof.share.utils.ServiceLocator;
import com.kynsoft.finamer.invoicing.application.command.manageAttachment.create.CreateAttachmentCommand;
import com.kynsoft.finamer.invoicing.application.command.manageInvoice.reconcileAuto.InvoiceReconcileAutomaticRequest;
import com.kynsoft.finamer.invoicing.application.query.invoiceReconcile.processstatus.automatic.InvoiceReconcileAutomaticImportProcessStatusRequest;
import com.kynsoft.finamer.invoicing.application.query.invoiceReconcile.processstatus.automatic.InvoiceReconcileAutomaticImportProcessStatusResponse;
import com.kynsoft.finamer.invoicing.application.query.invoiceReconcile.reconcileError.automatic.InvoiceReconcileAutomaticImportErrorRequest;
import com.kynsoft.finamer.invoicing.domain.dto.InvoiceReconcileAutomaticImportProcessDto;
import com.kynsoft.finamer.invoicing.domain.dto.ManageAttachmentTypeDto;
import com.kynsoft.finamer.invoicing.domain.dto.ManageInvoiceDto;
import com.kynsoft.finamer.invoicing.domain.dto.ResourceTypeDto;
import com.kynsoft.finamer.invoicing.domain.dtoEnum.EInvoiceReportType;
import com.kynsoft.finamer.invoicing.domain.dtoEnum.EProcessStatus;
import com.kynsoft.finamer.invoicing.domain.event.importStatus.CreateImportReconcileAutomaticStatusEvent;
import com.kynsoft.finamer.invoicing.domain.services.IInvoiceReconcileAutomaticService;
import com.kynsoft.finamer.invoicing.domain.services.IInvoiceReport;
import com.kynsoft.finamer.invoicing.domain.services.IManageAttachmentTypeService;
import com.kynsoft.finamer.invoicing.domain.services.IManageBookingService;
import com.kynsoft.finamer.invoicing.domain.services.IManageInvoiceService;
import com.kynsoft.finamer.invoicing.domain.services.IManageResourceTypeService;
import com.kynsoft.finamer.invoicing.infrastructure.excel.validators.reconcileauto.ReconcileAutomaticValidatorFactory;
import com.kynsoft.finamer.invoicing.infrastructure.identity.redis.reconcile.automatic.InvoiceReconcileAutomaticImportErrorEntity;
import com.kynsoft.finamer.invoicing.infrastructure.identity.redis.reconcile.automatic.InvoiceReconcileAutomaticImportProcessStatusRedisEntity;
import com.kynsoft.finamer.invoicing.infrastructure.repository.redis.reconcileAutomatic.reconcile.InvoiceReconcileAutomaticImportErrorRedisRepository;
import com.kynsoft.finamer.invoicing.infrastructure.repository.redis.reconcileAutomatic.reconcile.InvoiceReconcileAutomaticImportProcessStatusRedisRepository;
import com.kynsoft.finamer.invoicing.infrastructure.services.kafka.producer.manageInvoice.ProducerReplicateManageInvoiceService;
import com.kynsoft.finamer.invoicing.infrastructure.services.kafka.producer.manageInvoice.autoRec.ProducerManageInvoiceAutoRecService;
import com.kynsoft.finamer.invoicing.infrastructure.services.report.factory.InvoiceReportProviderFactory;
import com.kynsoft.finamer.invoicing.infrastructure.utils.InvoiceUploadAttachmentUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class InvoiceReconcileAutomaticServiceImpl implements IInvoiceReconcileAutomaticService {

    private final Logger log = LoggerFactory.getLogger(InvoiceReconcileAutomaticServiceImpl.class);
    private final ApplicationEventPublisher applicationEventPublisher;
    private final ReconcileAutomaticValidatorFactory reconcileAutomaticValidatorFactory;
    private final InvoiceReportProviderFactory invoiceReportProviderFactory;
    private final InvoiceUploadAttachmentUtil invoiceUploadAttachmentUtil;
    private final IManageAttachmentTypeService typeService;
    private final IManageInvoiceService manageInvoiceService;
    private final InvoiceReconcileAutomaticImportErrorRedisRepository errorRedisRepository;
    private final InvoiceReconcileAutomaticImportProcessStatusRedisRepository statusRedisRepository;
    private final ServiceLocator<IMediator> serviceLocator;
    private final IManageResourceTypeService resourceTypeService;
    private final IManageBookingService bookingService;
    private final ProducerManageInvoiceAutoRecService producerManageInvoiceAutoRecService;

    @Value("${resource.type.code}")
    private String paymentInvoiceTypeCode;

    @Value("${attachment.type.code}")
    private String attachmentTypeCode;

    private final ProducerReplicateManageInvoiceService producerUpdateManageInvoiceService;

    public InvoiceReconcileAutomaticServiceImpl(ApplicationEventPublisher applicationEventPublisher,
                                                ReconcileAutomaticValidatorFactory reconcileAutomaticValidatorFactory,
                                                InvoiceReportProviderFactory invoiceReportProviderFactory,
                                                InvoiceUploadAttachmentUtil invoiceUploadAttachmentUtil,
                                                IManageAttachmentTypeService typeService,
                                                IManageInvoiceService manageInvoiceService,
                                                InvoiceReconcileAutomaticImportErrorRedisRepository errorRedisRepository,
                                                InvoiceReconcileAutomaticImportProcessStatusRedisRepository statusRedisRepository,
                                                ServiceLocator<IMediator> serviceLocator, IManageResourceTypeService resourceTypeService,
                                                IManageBookingService bookingService, ProducerReplicateManageInvoiceService producerUpdateManageInvoiceService,
                                                ProducerManageInvoiceAutoRecService producerManageInvoiceAutoRecService
    ) {
        this.applicationEventPublisher = applicationEventPublisher;
        this.reconcileAutomaticValidatorFactory = reconcileAutomaticValidatorFactory;

        this.invoiceReportProviderFactory = invoiceReportProviderFactory;
        this.invoiceUploadAttachmentUtil = invoiceUploadAttachmentUtil;
        this.typeService = typeService;
        this.manageInvoiceService = manageInvoiceService;
        this.errorRedisRepository = errorRedisRepository;
        this.statusRedisRepository = statusRedisRepository;
        this.serviceLocator = serviceLocator;
        this.resourceTypeService = resourceTypeService;
        this.bookingService = bookingService;
        this.producerUpdateManageInvoiceService = producerUpdateManageInvoiceService;
        this.producerManageInvoiceAutoRecService = producerManageInvoiceAutoRecService;
    }

    @Override
    public void reconcileAutomatic(InvoiceReconcileAutomaticRequest request) {
        this.createImportProcessStatusEvent(
                InvoiceReconcileAutomaticImportProcessDto.builder()
                        .status(EProcessStatus.RUNNING)
                        .total(0)
                        .importProcessId(request.getImportProcessId()).build()
        );
        try {
            List<ManageInvoiceDto> validInvoice = this.readExcel(request);
            for (ManageInvoiceDto invoice : validInvoice) {
                this.createReconcileAutomaticSupport(invoice.getId().toString(), request);
                this.setInvoiceAutoRec(invoice.getId());
            }
            this.createImportProcessStatusEvent(
                    InvoiceReconcileAutomaticImportProcessDto.builder()
                            .total(validInvoice.size())
                            .status(EProcessStatus.FINISHED)
                            .importProcessId(request.getImportProcessId()).build()
            );
            this.releaseResource();

        }catch (Exception e){
            e.printStackTrace();
            log.error(e.getMessage());
            this.createImportProcessStatusEvent(
                    InvoiceReconcileAutomaticImportProcessDto.builder()
                            .status(EProcessStatus.FINISHED)
                            .total(0)
                            .hasError(true)
                            .exceptionMessage("The Reconcile Process failed")
                            .importProcessId(request.getImportProcessId())
                            .build()
            );
            this.releaseResource();
        }
    }

    private List<ManageInvoiceDto> readExcel(InvoiceReconcileAutomaticRequest request) throws Exception {
        List<ManageInvoiceDto> validInvoice = new ArrayList<>();
        reconcileAutomaticValidatorFactory.prepareValidator(request.getFileContent());
        Arrays.stream(request.getInvoiceIds()).forEach(id->{
            log.info("id --->"+id);
            log.info("id - length ----->",id.length());
        });
        List<ManageInvoiceDto> selectedInvoice = manageInvoiceService.findByIds(Arrays.stream(request.getInvoiceIds()).map(id->UUID.fromString(id.trim())).toList());

        for (ManageInvoiceDto manageInvoiceDto : selectedInvoice) {
            if (reconcileAutomaticValidatorFactory.validate(manageInvoiceDto, request.getImportProcessId())) {
                validInvoice.add(manageInvoiceDto);
                manageInvoiceDto.getBookings().forEach(bookingService::update);
            }
        }
        return validInvoice;
    }

    private void createReconcileAutomaticSupport(String invoiceId, InvoiceReconcileAutomaticRequest request) throws Exception{
        Optional<byte[]> fileContent = createInvoiceReconcileAutomaticSupportAttachmentContent(invoiceId);
        if (fileContent.isPresent()){
            createAttachmentForInvoice(invoiceId, request.getEmployeeId(), request.getEmployeeName(), fileContent.get());
        }
    }

    private Optional<byte[]> createInvoiceReconcileAutomaticSupportAttachmentContent(String invoiceId) {
        IInvoiceReport service = invoiceReportProviderFactory.getInvoiceReportService(EInvoiceReportType.RECONCILE_AUTO);
        return service.generateReport(invoiceId);
    }

    private void createAttachmentForInvoice(String invoiceId, String employeeId, String employeeName, byte[] fileContent) throws Exception {
        Optional<ManageAttachmentTypeDto> attachmentTypeDto = typeService.findByCode(attachmentTypeCode);
        ResourceTypeDto resourceTypeDto = resourceTypeService.findByCode(paymentInvoiceTypeCode);
        if (attachmentTypeDto.isPresent()) {
            LinkedHashMap<String, String> response = invoiceUploadAttachmentUtil.uploadAttachmentContent("Reconcile automatic.pdf", fileContent);
            CreateAttachmentCommand createAttachmentCommand = new CreateAttachmentCommand("Reconcile Automatic Support.pdf", response.get("url"),
                    "Importer by reconcile automatic", attachmentTypeDto.get().getId(),
                    UUID.fromString(invoiceId), employeeName, UUID.fromString(employeeId), resourceTypeDto.getId());
            IMediator mediator = serviceLocator.getBean(IMediator.class);
            mediator.send(createAttachmentCommand);
        }
    }

    private void createImportProcessStatusEvent(InvoiceReconcileAutomaticImportProcessDto dto) {
        CreateImportReconcileAutomaticStatusEvent createImportStatusEvent = new CreateImportReconcileAutomaticStatusEvent(this, dto);
        applicationEventPublisher.publishEvent(createImportStatusEvent);
    }

    private void releaseResource(){
        try {
            reconcileAutomaticValidatorFactory.releaseResources();
        } catch (IOException e) {
            log.error(e.getLocalizedMessage());
        }
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
            return new InvoiceReconcileAutomaticImportProcessStatusResponse(statusDtp.get().getStatus().name(),statusDtp.get().getTotal());
        }
        return null;
    }

    private void setInvoiceAutoRec(UUID invoiceId){
        ManageInvoiceDto invoiceDto = this.manageInvoiceService.findById(invoiceId);
        invoiceDto.setAutoRec(true);
        this.manageInvoiceService.update(invoiceDto);
        this.producerManageInvoiceAutoRecService.create(invoiceDto);
        //this.producerUpdateManageInvoiceService.create(this.manageInvoiceService.findById(invoiceDto.getId()));
    }
}
