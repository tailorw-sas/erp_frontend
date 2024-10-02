package com.kynsoft.finamer.invoicing.application.command.manageInvoice.reconcileManual;

import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.finamer.invoicing.domain.dto.*;
import com.kynsoft.finamer.invoicing.domain.dtoEnum.EInvoiceStatus;
import com.kynsoft.finamer.invoicing.domain.services.*;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class ReconcileManualCommandHandler implements ICommandHandler<ReconcileManualCommand> {

    private final IManageInvoiceService invoiceService;
    private final IManageAttachmentTypeService attachmentTypeService;
    private final IManageResourceTypeService resourceTypeService;
    private final IManageInvoiceStatusService invoiceStatusService;

    public ReconcileManualCommandHandler(IManageInvoiceService invoiceService, IManageAttachmentTypeService attachmentTypeService, IManageResourceTypeService resourceTypeService, IManageInvoiceStatusService invoiceStatusService) {
        this.invoiceService = invoiceService;
        this.attachmentTypeService = attachmentTypeService;
        this.resourceTypeService = resourceTypeService;
        this.invoiceStatusService = invoiceStatusService;
    }

    @Override
    public void handle(ReconcileManualCommand command) {
        Map<UUID, String> errors = new HashMap<>();
        for (UUID id : command.getInvoices()){
            ManageInvoiceDto invoiceDto = this.invoiceService.findById(id);
            if(!invoiceDto.getAgency().getAutoReconcile()){
                errors.put(invoiceDto.getId(), "The agency does not have auto-reconciliation enabled.");
                continue;
            }
            if(invoiceDto.getStatus().compareTo(EInvoiceStatus.PROCECSED) != 0){
                errors.put(invoiceDto.getId(), "The invoice is not in processed status.");
                continue;
            }
            try{
                //TODO: obtener el pdf
            } catch (Exception e){
                errors.put(invoiceDto.getId(), "The pdf could not be generated.");
                continue;
            }

            String filename = "invoice_" + invoiceDto.getInvoiceId() + ".pdf";
            String file = "";
            try {
                //TODO: subir el archivo y obtener los datos para el attachment
            } catch (Exception e) {
                errors.put(invoiceDto.getId(), "The attachment could not be uploaded.");
                continue;
            }

            //creando y adjuntando el attachment
            ManageAttachmentTypeDto attachmentTypeDto = command.getAttachInvDefault() != null
                    ? this.attachmentTypeService.findById(command.getAttachInvDefault())
                    : null;
            ResourceTypeDto resourceTypeDto = command.getResourceType() != null
                    ? this.resourceTypeService.findById(command.getResourceType())
                    : null;
            List<ManageAttachmentDto> attachments = invoiceDto.getAttachments();
            attachments.add(new ManageAttachmentDto(
                    UUID.randomUUID(),
                    null,
                    filename,
                    file,
                    "",
                    attachmentTypeDto,
                    null,
                    command.getEmployeeName(),
                    command.getEmployeeId(),
                    null,
                    resourceTypeDto
            ));
            invoiceDto.setStatus(EInvoiceStatus.RECONCILED);
            ManageInvoiceStatusDto invoiceStatusDto = this.invoiceStatusService.findByEInvoiceStatus(EInvoiceStatus.RECONCILED);
            invoiceDto.setManageInvoiceStatus(invoiceStatusDto);
            this.invoiceService.update(invoiceDto);
        }
        command.setErrors(errors);
    }
}
