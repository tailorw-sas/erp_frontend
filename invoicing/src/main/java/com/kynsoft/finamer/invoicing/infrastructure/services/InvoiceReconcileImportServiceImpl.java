package com.kynsoft.finamer.invoicing.infrastructure.services;

import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsoft.finamer.invoicing.application.command.invoiceReconcileImport.importReconcile.InvoiceReconcileImportRequest;
import com.kynsoft.finamer.invoicing.application.query.invoiceReconcile.processstatus.InvoiceReconcileImportProcessStatusRequest;
import com.kynsoft.finamer.invoicing.application.query.invoiceReconcile.processstatus.InvoiceReconcileImportProcessStatusResponse;
import com.kynsoft.finamer.invoicing.application.query.invoiceReconcile.reconcileError.InvoiceReconcileImportErrorRequest;
import com.kynsoft.finamer.invoicing.domain.dto.InvoiceReconcileImportProcessStatusDto;
import com.kynsoft.finamer.invoicing.domain.dto.ManageInvoiceDto;
import com.kynsoft.finamer.invoicing.domain.dtoEnum.EProcessStatus;
import com.kynsoft.finamer.invoicing.domain.event.createAttachment.CreateAttachmentEvent;
import com.kynsoft.finamer.invoicing.domain.event.importError.CreateImportErrorEvent;
import com.kynsoft.finamer.invoicing.domain.event.importStatus.CreateImportStatusEvent;
import com.kynsoft.finamer.invoicing.domain.services.IManageInvoiceService;
import com.kynsoft.finamer.invoicing.domain.services.InvoiceReconcileImportService;
import com.kynsoft.finamer.invoicing.domain.services.StorageService;
import com.kynsoft.finamer.invoicing.infrastructure.identity.redis.reconcile.InvoiceReconcileImportError;
import com.kynsoft.finamer.invoicing.infrastructure.identity.redis.reconcile.InvoiceReconcileImportProcessStatusRedisEntity;
import com.kynsoft.finamer.invoicing.infrastructure.repository.redis.reconcile.InvoiceReconcileImportErrorRedisRepository;
import com.kynsoft.finamer.invoicing.infrastructure.repository.redis.reconcile.InvoiceReconcileImportProcessStatusRedisRepository;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.Optional;
import java.util.UUID;

@Service
public class InvoiceReconcileImportServiceImpl implements InvoiceReconcileImportService {
    private final Logger log = LoggerFactory.getLogger(InvoiceReconcileImportService.class);

    private final InvoiceReconcileImportProcessStatusRedisRepository statusRedisRepository;
    private final InvoiceReconcileImportErrorRedisRepository errorRedisRepository;

    private final IManageInvoiceService invoiceService;

    private final StorageService storageService;

    private final ApplicationEventPublisher applicationEventPublisher;

    public InvoiceReconcileImportServiceImpl(InvoiceReconcileImportProcessStatusRedisRepository statusRedisRepository,
                                             InvoiceReconcileImportErrorRedisRepository errorRedisRepository,
                                             IManageInvoiceService invoiceService, StorageService storageService,
                                             ApplicationEventPublisher applicationEventPublisher) {
        this.statusRedisRepository = statusRedisRepository;
        this.errorRedisRepository = errorRedisRepository;
        this.invoiceService = invoiceService;
        this.storageService = storageService;
        this.applicationEventPublisher = applicationEventPublisher;
    }

    @Override
    @Async
    public void importReconcileFromFile(InvoiceReconcileImportRequest request) {
        this.createImportProcessStatusEvent(
                InvoiceReconcileImportProcessStatusDto.builder()
                        .status(EProcessStatus.RUNNING)
                        .importProcessId(request.getImportProcessId()).build()
        );
        storageService.loadAll(request.getImportProcessId())
                .parallel()
                .map(Path::toFile)
                .filter(file -> this.isAttachmentValid(file, request.getImportProcessId()))
                .forEach(attachmentFile -> createInvoiceAttachment(attachmentFile, request));
        this.cleanImportationResource(request.getImportProcessId());
        this.createImportProcessStatusEvent(
                InvoiceReconcileImportProcessStatusDto.builder()
                        .status(EProcessStatus.FINISHED)
                        .importProcessId(request.getImportProcessId()).build()
        );
    }

    private void createImportProcessStatusEvent(InvoiceReconcileImportProcessStatusDto dto) {
        CreateImportStatusEvent createImportStatusEvent = new CreateImportStatusEvent(this, dto);
        applicationEventPublisher.publishEvent(createImportStatusEvent);
    }

    private boolean isAttachmentValid(File file, String importProcessId) {
        try {
            return invoiceService.existManageInvoiceByInvoiceId(Long.parseLong(getInvoiceIdFromFileName(file)));
        } catch (Exception e) {
            this.processError("File name is not valid " + getInvoiceIdFromFileName(file), importProcessId);
            return false;
        }
    }

    private String getInvoiceIdFromFileName(File file) {
        return FilenameUtils.removeExtension(file.getName());
    }

    private void createInvoiceAttachment(File attachment, InvoiceReconcileImportRequest request) {
        try (InputStream inputStream = storageService.loadAsResource(attachment.getName(), request.getImportProcessId())) {
            ManageInvoiceDto manageInvoiceDto = invoiceService.findByInvoiceId(Long.parseLong(getInvoiceIdFromFileName(attachment)));
            CreateAttachmentEvent createAttachmentEvent = new CreateAttachmentEvent(this,
                    manageInvoiceDto.getId(),
                    request.getEmployee(),
                    UUID.fromString(request.getEmployeeId()),
                    String.valueOf(getInvoiceIdFromFileName(attachment)),
                    "Uploaded from file",
                    inputStream.readAllBytes()
            );
            applicationEventPublisher.publishEvent(createAttachmentEvent);
        } catch (Exception e) {
            this.createImportProcessStatusEvent(
                    InvoiceReconcileImportProcessStatusDto.builder()
                            .status(EProcessStatus.FINISHED)
                            .hasError(true)
                            .exceptionMessage("The attachment could not be created")
                            .importProcessId(request.getImportProcessId()).build()
            );

        }


    }

    private void processError(String message, String importProcessId) {
        log.error(message);
        InvoiceReconcileImportError error = new InvoiceReconcileImportError(null, importProcessId, message);
        CreateImportErrorEvent createImportErrorEvent = new CreateImportErrorEvent(this, error);
        applicationEventPublisher.publishEvent(createImportErrorEvent);
    }

    private void cleanImportationResource(String importProcessId) {
        storageService.deleteAll(importProcessId);
    }


    @Override
    public PaginatedResponse getReconcileImportErrors(InvoiceReconcileImportErrorRequest request) {
        Page<InvoiceReconcileImportError> importErrorPages = errorRedisRepository.
                findAllByImportProcessId(request.getQuery(),
                        request.getPageable());
        return new PaginatedResponse(importErrorPages.getContent(),
                importErrorPages.getTotalPages(),
                importErrorPages.getNumberOfElements(),
                importErrorPages.getTotalElements(),
                importErrorPages.getSize(),
                importErrorPages.getNumber());
    }

    @Override
    public InvoiceReconcileImportProcessStatusResponse getReconcileImportProcessStatus(InvoiceReconcileImportProcessStatusRequest request) {
        Optional<InvoiceReconcileImportProcessStatusRedisEntity> entity =
                statusRedisRepository.findByImportProcessId(request.getImportProcessId());

        if (entity.isPresent()) {
            if (entity.get().isHasError()) {
                throw new RuntimeException(entity.get().getExceptionMessage());
            }
            return new InvoiceReconcileImportProcessStatusResponse(entity.get().getStatus().name());
        }
        return null;
    }


}
