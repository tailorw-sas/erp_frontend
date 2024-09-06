package com.kynsoft.finamer.invoicing.infrastructure.services;

import com.kynsof.share.core.domain.exception.ExcelException;
import com.kynsof.share.core.domain.response.PaginatedResponse;
import com.kynsoft.finamer.invoicing.application.command.invoiceReconcileImport.importReconcile.InvoiceReconcileImportCommand;
import com.kynsoft.finamer.invoicing.application.command.invoiceReconcileImport.importReconcile.InvoiceReconcileImportRequest;
import com.kynsoft.finamer.invoicing.application.query.invoiceReconcile.processstatus.InvoiceReconcileImportProcessStatusRequest;
import com.kynsoft.finamer.invoicing.application.query.invoiceReconcile.processstatus.InvoiceReconcileImportProcessStatusResponse;
import com.kynsoft.finamer.invoicing.application.query.invoiceReconcile.reconcileError.InvoiceReconcileImportErrorRequest;
import com.kynsoft.finamer.invoicing.application.query.manageBooking.importbooking.ImportBookingProcessStatusResponse;
import com.kynsoft.finamer.invoicing.domain.dto.BookingImportProcessDto;
import com.kynsoft.finamer.invoicing.domain.dto.ManageInvoiceDto;
import com.kynsoft.finamer.invoicing.domain.event.createAttachment.CreateAttachmentEvent;
import com.kynsoft.finamer.invoicing.domain.services.IManageInvoiceService;
import com.kynsoft.finamer.invoicing.domain.services.InvoiceReconcileImportService;
import com.kynsoft.finamer.invoicing.domain.services.StorageService;
import com.kynsoft.finamer.invoicing.infrastructure.identity.redis.excel.BookingImportProcessRedisEntity;
import com.kynsoft.finamer.invoicing.infrastructure.identity.redis.excel.BookingRowError;
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
import java.io.FileInputStream;
import java.io.IOException;
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
        storageService.loadAll(request.getImportProcessId())
                .parallel()
                .map(Path::toFile )
                .filter(this::isAttachmentValid)
                .forEach(attachmentFile->createInvoiceAttachment(attachmentFile,request));
        this.cleanImportationResource(request.getImportProcessId());
    }

    private boolean isAttachmentValid(File file){
        try {
            return invoiceService.existManageInvoiceByInvoiceId(getInvoiceIdFromFileName(file));
        }catch (Exception e){
            log.error(e.getMessage());
            return false;
        }
    }
    private long getInvoiceIdFromFileName(File file){
        String fileName = FilenameUtils.removeExtension(file.getName());
        return Long.parseLong(fileName);
    }
    private void createInvoiceAttachment(File attachment, InvoiceReconcileImportRequest request){
        try (FileInputStream fileInputStream = new FileInputStream(attachment)) {
            ManageInvoiceDto manageInvoiceDto = invoiceService.findByInvoiceId(getInvoiceIdFromFileName(attachment));
            CreateAttachmentEvent createAttachmentEvent = CreateAttachmentEvent.builder()
                    .invoiceId(manageInvoiceDto.getId())
                    .employee(request.getEmployee())
                    .remarks("Uploaded from file")
                    .employeeId(UUID.fromString(request.getEmployeeId()))
                    .fileName(String.valueOf(getInvoiceIdFromFileName(attachment)))
                    .file(fileInputStream.readAllBytes())
                    .build();
            applicationEventPublisher.publishEvent(createAttachmentEvent);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


    }

    private void cleanImportationResource(String importProcessId){
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

         if (entity.isPresent()){
             if (entity.get().isHasError()) {
                 throw new RuntimeException(entity.get().getExceptionMessage());
             }
             return new InvoiceReconcileImportProcessStatusResponse(entity.get().getStatus().name());
         }
        return null;
    }


}
