package com.kynsoft.finamer.payment.application.command.attachmentStatusHistory.create;

import com.kynsof.share.core.domain.bus.command.ICommand;
import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import com.kynsoft.finamer.payment.domain.dto.ManageEmployeeDto;
import com.kynsoft.finamer.payment.domain.dto.PaymentDto;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class CreateAttachmentStatusHistoryCommand implements ICommand {

    private PaymentDto payment;
    private UUID id;

    private ManageEmployeeDto employeeDto;

    public CreateAttachmentStatusHistoryCommand(PaymentDto payment, ManageEmployeeDto employeeDto) {
        this.id = UUID.randomUUID();
        this.payment = payment;
        this.employeeDto = employeeDto;
    }

    @Override
    public ICommandMessage getMessage() {
        return new CreateAttachmentStatusHistoryMessage(id);
    }
}
