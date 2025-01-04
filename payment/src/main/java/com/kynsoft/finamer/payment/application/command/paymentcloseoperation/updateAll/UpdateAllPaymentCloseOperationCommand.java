package com.kynsoft.finamer.payment.application.command.paymentcloseoperation.updateAll;

import com.kynsof.share.core.domain.bus.command.ICommand;
import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import com.kynsoft.finamer.payment.domain.dtoEnum.Status;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class UpdateAllPaymentCloseOperationCommand implements ICommand {

    private List<UUID> hotels;
    private Status status;
    private LocalDate beginDate;
    private LocalDate endDate;

    public UpdateAllPaymentCloseOperationCommand(Status status, List<UUID> hotels, LocalDate beginDate, LocalDate endDate) {
        this.status = status;
        this.hotels = hotels;
        this.beginDate = beginDate;
        this.endDate = endDate;
    }

    public static UpdateAllPaymentCloseOperationCommand fromRequest(UpdateAllPaymentCloseOperationRequest request) {
        return new UpdateAllPaymentCloseOperationCommand(
                request.getStatus(),
                request.getHotels(),
                request.getBeginDate(),
                request.getEndDate()
        );
    }

    @Override
    public ICommandMessage getMessage() {
        return new UpdateAllPaymentCloseOperationMessage();
    }
}
