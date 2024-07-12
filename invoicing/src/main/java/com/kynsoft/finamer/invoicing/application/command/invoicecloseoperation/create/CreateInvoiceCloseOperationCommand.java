package com.kynsoft.finamer.invoicing.application.command.invoicecloseoperation.create;

import com.kynsof.share.core.domain.bus.command.ICommand;
import com.kynsof.share.core.domain.bus.command.ICommandMessage;
import com.kynsoft.finamer.invoicing.domain.dtoEnum.Status;
import java.time.LocalDate;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class CreateInvoiceCloseOperationCommand implements ICommand {

    private UUID id;
    private Status status;
    private UUID hotel;
    private LocalDate beginDate;
    private LocalDate endDate;

    public CreateInvoiceCloseOperationCommand(Status status, UUID hotel, LocalDate beginDate, LocalDate endDate) {
        this.id = UUID.randomUUID();
        this.status = status;
        this.hotel = hotel;
        this.beginDate = beginDate;
        this.endDate = endDate;
    }

    public static CreateInvoiceCloseOperationCommand fromRequest(CreateInvoiceCloseOperationRequest request) {
        return new CreateInvoiceCloseOperationCommand(
                request.getStatus(),
                request.getHotel(),
                request.getBeginDate(),
                request.getEndDate()
        );
    }

    @Override
    public ICommandMessage getMessage() {
        return new CreateInvoiceCloseOperationMessage(id);
    }
}
