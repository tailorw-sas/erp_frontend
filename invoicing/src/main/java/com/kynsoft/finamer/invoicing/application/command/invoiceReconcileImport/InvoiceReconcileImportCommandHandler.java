package com.kynsoft.finamer.invoicing.application.command.invoiceReconcileImport;

import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.finamer.invoicing.domain.dto.ManageInvoiceDto;
import com.kynsoft.finamer.invoicing.domain.event.createAttachment.CreateAttachmentEvent;
import com.kynsoft.finamer.invoicing.domain.services.IManageInvoiceService;
import com.kynsoft.finamer.invoicing.domain.services.StorageService;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.UUID;

@Component
public class InvoiceReconcileImportCommandHandler implements ICommandHandler<InvoiceReconcileImportCommand> {
    private final Logger log = LoggerFactory.getLogger(InvoiceReconcileImportCommandHandler.class);
    private final StorageService storageService;
    private final IManageInvoiceService invoiceService;
    private final ApplicationEventPublisher applicationEventPublisher;
    public InvoiceReconcileImportCommandHandler(StorageService storageService,
                                                IManageInvoiceService invoiceService,
                                                ApplicationEventPublisher applicationEventPublisher) {
        this.storageService = storageService;
        this.invoiceService = invoiceService;
        this.applicationEventPublisher = applicationEventPublisher;
    }

    @Override
    public void handle(InvoiceReconcileImportCommand command) {

        storageService.loadAll(command.getImportProcessId())
                .parallel()
                .map(Path::toFile )
                .filter(this::isAttachmentValid)
                .forEach(attachmentFile->createInvoiceAttachment(attachmentFile,command));
        this.cleanImportationResource(command.getImportProcessId());
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
    private void createInvoiceAttachment(File attachment,InvoiceReconcileImportCommand command){
            try (FileInputStream fileInputStream = new FileInputStream(attachment)) {
                ManageInvoiceDto manageInvoiceDto = invoiceService.findByInvoiceId(getInvoiceIdFromFileName(attachment));
                CreateAttachmentEvent createAttachmentEvent = CreateAttachmentEvent.builder()
                        .invoiceId(manageInvoiceDto.getId())
                        .employee(command.getEmployee())
                        .remarks("Uploaded from file")
                        .employeeId(UUID.fromString(command.getEmployeeId()))
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
}
