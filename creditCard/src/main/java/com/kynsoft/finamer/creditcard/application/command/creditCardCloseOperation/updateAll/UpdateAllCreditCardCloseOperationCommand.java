package com.kynsoft.finamer.creditcard.application.command.creditCardCloseOperation.updateAll;

import com.kynsof.share.core.domain.bus.command.ICommand;
import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import com.kynsoft.finamer.creditcard.domain.dtoEnum.Status;
import java.time.LocalDate;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class UpdateAllCreditCardCloseOperationCommand implements ICommand {

    private List<UUID> hotels;
    private Status status;
    private LocalDate beginDate;
    private LocalDate endDate;

    public UpdateAllCreditCardCloseOperationCommand(Status status, List<UUID> hotels, LocalDate beginDate, LocalDate endDate) {
        this.status = status;
        this.hotels = hotels;
        this.beginDate = beginDate;
        this.endDate = endDate;
    }

    public static UpdateAllCreditCardCloseOperationCommand fromRequest(UpdateAllCreditCardCloseOperationRequest request) {
        return new UpdateAllCreditCardCloseOperationCommand(
                request.getStatus(),
                request.getHotels(),
                request.getBeginDate(),
                request.getEndDate()
        );
    }

    @Override
    public ICommandMessage getMessage() {
        return new UpdateAllCreditCardCloseOperationMessage();
    }
}
