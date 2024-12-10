package com.kynsoft.finamer.payment.application.command.attachment.create;

import com.kynsof.share.core.domain.bus.command.ICommandHandler;
import com.kynsoft.finamer.payment.application.command.payment.create.CreateAttachmentRequest;
import com.kynsoft.finamer.payment.domain.dto.AttachmentTypeDto;
import com.kynsoft.finamer.payment.domain.dto.MasterPaymentAttachmentDto;
import com.kynsoft.finamer.payment.domain.dto.ResourceTypeDto;
import com.kynsoft.finamer.payment.domain.dtoEnum.Status;
import com.kynsoft.finamer.payment.domain.services.IManageAttachmentTypeService;
import com.kynsoft.finamer.payment.domain.services.IManageResourceTypeService;
import com.kynsoft.finamer.payment.domain.services.IMasterPaymentAttachmentService;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Component
public class CreateAttachmentCommandHandler implements ICommandHandler<CreateAttachmentCommand> {

    private final IMasterPaymentAttachmentService masterPaymentAttachmentService;
    private final IManageAttachmentTypeService manageAttachmentTypeService;
    private final IManageResourceTypeService manageResourceTypeService;

    public CreateAttachmentCommandHandler(IMasterPaymentAttachmentService masterPaymentAttachmentService,
                                          IManageAttachmentTypeService manageAttachmentTypeService,
                                          IManageResourceTypeService manageResourceTypeService) {
        this.masterPaymentAttachmentService = masterPaymentAttachmentService;
        this.manageAttachmentTypeService = manageAttachmentTypeService;
        this.manageResourceTypeService = manageResourceTypeService;
    }

    @Override
    public void handle(CreateAttachmentCommand command) {
        List<MasterPaymentAttachmentDto> dtos = new ArrayList<>();
        AttachmentTypeDto attachmentTypeSupport = this.manageAttachmentTypeService.getByDefault();
        AttachmentTypeDto attachmentTypeOtherDto = this.manageAttachmentTypeService.getByAntiToIncomeImport();
        ResourceTypeDto resourceTypeDto = this.manageResourceTypeService.getByDefault();
        for (CreateAttachmentRequest attachment : command.getAttachments()) {
            dtos.add(new MasterPaymentAttachmentDto(
                    UUID.randomUUID(),
                    Status.ACTIVE,
                    command.getPaymentDto(),
                    resourceTypeDto,
                    attachment.isSupport() ? attachmentTypeSupport : attachmentTypeOtherDto,
                    attachment.getFileName(),
                    attachment.getFileWeight(),
                    attachment.getPath(),
                    attachment.getRemark(),
                    0L
            ));
        }

        this.masterPaymentAttachmentService.create(dtos);
        command.setDtos(dtos);
    }

}
