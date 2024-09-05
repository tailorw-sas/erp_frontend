package com.kynsoft.finamer.invoicing.application.command.invoiceReconcileImport;

import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.finamer.invoicing.application.command.manageAttachment.create.CreateAttachmentCommand;
import com.kynsoft.finamer.invoicing.domain.dto.ManageInvoiceDto;
import com.kynsoft.finamer.invoicing.domain.services.IManageInvoiceService;
import com.kynsoft.finamer.invoicing.domain.services.StorageService;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.File;

@Component
public class InvoiceReconcileImportCommandHandler implements ICommandHandler<InvoiceReconcileImportCommand> {

    private final Logger log = LoggerFactory.getLogger(InvoiceReconcileImportCommandHandler.class);
    private final StorageService storageService;
    private final IManageInvoiceService invoiceService;
    public InvoiceReconcileImportCommandHandler(StorageService storageService,
                                                IManageInvoiceService invoiceService) {
        this.storageService = storageService;
        this.invoiceService = invoiceService;
    }

    @Override
    public void handle(InvoiceReconcileImportCommand command) {

        storageService.loadAll()
                .parallel()
                .filter(path -> isAttachmentValid(path.toFile()))
                .map(path ->getInvoiceIdFromFileName(path.toFile()) )
                .forEach(this::createInvoiceAttachment);
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
    private void createInvoiceAttachment(long invoiceId){
        ManageInvoiceDto manageInvoiceDto =invoiceService.findByInvoiceId(invoiceId);
        CreateAttachmentCommand command
                =new CreateAttachmentCommand();
    }
}
