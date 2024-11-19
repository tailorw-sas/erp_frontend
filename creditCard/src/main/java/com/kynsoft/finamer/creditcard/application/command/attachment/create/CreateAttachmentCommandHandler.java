package com.kynsoft.finamer.creditcard.application.command.attachment.create;

import com.kynsof.share.core.domain.RulesChecker;
import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.finamer.creditcard.domain.dto.AttachmentDto;
import com.kynsoft.finamer.creditcard.domain.dto.ManageAttachmentTypeDto;
import com.kynsoft.finamer.creditcard.domain.dto.ResourceTypeDto;
import com.kynsoft.finamer.creditcard.domain.dto.TransactionDto;
import com.kynsoft.finamer.creditcard.domain.rules.attachment.AttachmentFileNameNotNullRule;
import com.kynsoft.finamer.creditcard.domain.services.*;
import org.springframework.stereotype.Component;

@Component
public class CreateAttachmentCommandHandler implements ICommandHandler<CreateAttachmentCommand> {

    private final IAttachmentService attachmentService;
    private final IManageAttachmentTypeService attachmentTypeService;
    private final IManageResourceTypeService resourceTypeService;
    private final ITransactionService transactionService;
//    private final IAttachmentStatusHistoryService attachmentStatusHistoryService;
    private final ITransactionStatusHistoryService transactionStatusHistoryService;

    public CreateAttachmentCommandHandler(IAttachmentService attachmentService,
                                          IManageAttachmentTypeService attachmentTypeService,
                                          ITransactionService transactionService,
                                          IManageResourceTypeService resourceTypeService,
//                                            IAttachmentStatusHistoryService attachmentStatusHistoryService,
                                          ITransactionStatusHistoryService transactionStatusHistoryService) {
        this.attachmentService = attachmentService;
        this.attachmentTypeService = attachmentTypeService;
        this.transactionService = transactionService;
        this.resourceTypeService = resourceTypeService;
//        this.attachmentStatusHistoryService = attachmentStatusHistoryService;
        this.transactionStatusHistoryService = transactionStatusHistoryService;
    }

    @Override
    public void handle(CreateAttachmentCommand command) {
        RulesChecker.checkRule(new AttachmentFileNameNotNullRule(command.getFile()));

        ManageAttachmentTypeDto attachmentType = command.getType() != null
                ? this.attachmentTypeService.findById(command.getType())
                : null;
        TransactionDto transactionDto = this.transactionService.findById(command.getTransaction());

        ResourceTypeDto resourceTypeDto = command.getPaymentResourceType() != null
                ? this.resourceTypeService.findById(command.getPaymentResourceType())
                : null;

            Long attachmentId = attachmentService.create(new AttachmentDto(
                    command.getId(),
                    null,
                    command.getFilename(),
                    command.getFile(),
                    command.getRemark(),
                    attachmentType,
                    transactionDto,
                    command.getEmployee(), 
                    command.getEmployeeId(),
                    null,
                    resourceTypeDto
            ));
//            this.updateAttachmentStatusHistory(transactionDto, command.getFilename(), attachmentId, command.getEmployee(), command.getEmployeeId());
    }

//    private void updateAttachmentStatusHistory(ManageInvoiceDto invoice, String fileName, Long attachmentId,
//            String employee, UUID employeeId) {
//
//        AttachmentStatusHistoryDto attachmentStatusHistoryDto = new AttachmentStatusHistoryDto();
//        attachmentStatusHistoryDto.setId(UUID.randomUUID());
//        attachmentStatusHistoryDto
//                .setDescription("An attachment to the invoice was inserted. The file name: " + fileName);
//        attachmentStatusHistoryDto.setEmployee(employee);
//        attachmentStatusHistoryDto.setInvoice(invoice);
//        attachmentStatusHistoryDto.setEmployeeId(employeeId);
//        attachmentStatusHistoryDto.setAttachmentId(attachmentId);
//
//        this.attachmentStatusHistoryService.create(attachmentStatusHistoryDto);
//    }

}
