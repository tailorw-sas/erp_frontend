package com.kynsoft.finamer.creditcard.application.command.creditCardCloseOperation.create;

import com.kynsof.share.core.domain.bus.command.ICommand;
import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import com.kynsoft.finamer.creditcard.domain.dtoEnum.Status;
import java.time.LocalDate;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class CreateCreditCardCloseOperationCommand implements ICommand {

    private UUID id;
    private Status status;
    private UUID hotel;
    private LocalDate beginDate;
    private LocalDate endDate;

    public CreateCreditCardCloseOperationCommand(Status status, UUID hotel, LocalDate beginDate, LocalDate endDate) {
        this.id = UUID.randomUUID();
        this.status = status;
        this.hotel = hotel;
        this.beginDate = beginDate;
        this.endDate = endDate;
    }

    public static CreateCreditCardCloseOperationCommand fromRequest(CreateCreditCardCloseOperationRequest request) {
        return new CreateCreditCardCloseOperationCommand(
                request.getStatus(),
                request.getHotel(),
                request.getBeginDate(),
                request.getEndDate()
        );
    }

    @Override
    public ICommandMessage getMessage() {
        return new CreateCreditCardCloseOperationMessage(id);
    }
}
