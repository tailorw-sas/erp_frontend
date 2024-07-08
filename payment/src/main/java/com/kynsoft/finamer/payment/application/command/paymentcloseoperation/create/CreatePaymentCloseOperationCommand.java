package com.kynsoft.finamer.payment.application.command.paymentcloseoperation.create;

import com.kynsof.share.core.domain.bus.command.ICommand;
import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import com.kynsoft.finamer.payment.domain.dtoEnum.Status;
import java.time.LocalDate;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class CreatePaymentCloseOperationCommand implements ICommand {

    private UUID id;
    private Status status;
    private UUID hotel;
    private LocalDate beginDate;
    private LocalDate endDate;

    public CreatePaymentCloseOperationCommand(Status status, UUID hotel, LocalDate beginDate, LocalDate endDate) {
        this.id = UUID.randomUUID();
        this.status = status;
        this.hotel = hotel;
        this.beginDate = beginDate;
        this.endDate = endDate;
    }

    public static CreatePaymentCloseOperationCommand fromRequest(CreatePaymentCloseOperationRequest request) {
        return new CreatePaymentCloseOperationCommand(
                request.getStatus(),
                request.getHotel(),
                request.getBeginDate(),
                request.getEndDate()
        );
    }

    @Override
    public ICommandMessage getMessage() {
        return new CreatePaymentCloseOperationMessage(id);
    }
}
