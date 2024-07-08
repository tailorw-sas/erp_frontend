package com.kynsoft.finamer.payment.application.command.paymentcloseoperation.update;

import com.kynsof.share.core.domain.bus.command.ICommand;
import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import com.kynsoft.finamer.payment.domain.dtoEnum.Status;
import java.time.LocalDate;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class UpdatePaymentCloseOperationCommand implements ICommand {
    private UUID id;
    private Status status;
    private UUID hotel;
    private LocalDate beginDate;
    private LocalDate endDate;

    public UpdatePaymentCloseOperationCommand(UUID id, Status status, UUID hotel, LocalDate beginDate, LocalDate endDate) {
        this.id = id;
        this.status = status;
        this.hotel = hotel;
        this.beginDate = beginDate;
        this.endDate = endDate;
    }

    public static UpdatePaymentCloseOperationCommand fromRequest(UpdatePaymentCloseOperationRequest request, UUID id) {
        return new UpdatePaymentCloseOperationCommand(
                id,
                request.getStatus(),
                request.getHotel(),
                request.getBeginDate(),
                request.getEndDate()
        );
    }

    @Override
    public ICommandMessage getMessage() {
        return new UpdatePaymentCloseOperationMessage(id);
    }
}
