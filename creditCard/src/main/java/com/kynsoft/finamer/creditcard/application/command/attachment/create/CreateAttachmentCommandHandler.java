package com.kynsoft.finamer.creditcard.application.command.attachment.create;

import com.kynsof.share.core.domain.RulesChecker;
import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.finamer.creditcard.domain.dto.*;
import com.kynsoft.finamer.creditcard.domain.rules.attachment.AttachmentFileNameNotNullRule;
import com.kynsoft.finamer.creditcard.domain.services.*;
import org.springframework.stereotype.Component;

@Component
public class CreateAttachmentCommandHandler implements ICommandHandler<CreateAttachmentCommand> {

    private final IAttachmentService attachmentService;
    private final IManageAttachmentTypeService attachmentTypeService;
    private final IManageResourceTypeService resourceTypeService;
    private final ITransactionService transactionService;
    private final IAttachmentStatusHistoryService attachmentStatusHistoryService;
    private final IHotelPaymentService hotelPaymentService;

    public CreateAttachmentCommandHandler(IAttachmentService attachmentService,
                                          IManageAttachmentTypeService attachmentTypeService,
                                          ITransactionService transactionService,
                                          IManageResourceTypeService resourceTypeService,
                                          IAttachmentStatusHistoryService attachmentStatusHistoryService, IHotelPaymentService hotelPaymentService) {
        this.attachmentService = attachmentService;
        this.attachmentTypeService = attachmentTypeService;
        this.transactionService = transactionService;
        this.resourceTypeService = resourceTypeService;
        this.attachmentStatusHistoryService = attachmentStatusHistoryService;
        this.hotelPaymentService = hotelPaymentService;
    }

    @Override
    public void handle(CreateAttachmentCommand command) {
        RulesChecker.checkRule(new AttachmentFileNameNotNullRule(command.getFile()));

        ManageAttachmentTypeDto attachmentType = command.getType() != null
                ? this.attachmentTypeService.findById(command.getType())
                : null;
        TransactionDto transactionDto = command.getTransaction() != null ? this.transactionService.findById(command.getTransaction()) : null;
        HotelPaymentDto hotelPaymentDto = command.getHotelPayment() != null ? this.hotelPaymentService.findById(command.getHotelPayment()) : null;

        ResourceTypeDto resourceTypeDto = command.getPaymentResourceType() != null
                ? this.resourceTypeService.findById(command.getPaymentResourceType())
                : null;

            AttachmentDto attachmentDto = attachmentService.create(new AttachmentDto(
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
                    resourceTypeDto,
                    hotelPaymentDto
            ));
            this.attachmentStatusHistoryService.create(attachmentDto, "inserted");
    }
}
